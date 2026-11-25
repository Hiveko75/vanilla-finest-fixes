package io.github.hiveko.vanillafinest.fixes.texts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hard-coded texts module: makes texts that other mods write straight into their code translatable, so a
 * resource pack (the Hungarian Translations pack) can translate them like any other text.
 *
 * <p>{@value #FILE} (in platform/mc&lt;version&gt;/resources) lists groups: a mod, the classes that draw its
 * texts, and the texts with a key name each. When one of those classes loads and the mod is installed,
 * {@link #patch} changes the class before it is used:
 * <ul>
 *   <li>a text constant from the list is passed through {@link Texts#tr}, which returns the translation of
 *       {@code vanilla-finest-fixes.<group>.<name>}, or the mod's own text when there is none;</li>
 *   <li>a text the mod builds with values ({@code "Delete " + n + " screenshots?"}, listed as
 *       {@code "Delete %s screenshots?"}) is passed through {@link Texts#concat}, which puts the same values
 *       into the translation;</li>
 *   <li>with {@code "literal": true}, every text the class gives to {@code Component.literal} is looked up
 *       too ({@link Texts#literal}), for texts made at run time (Better Advancements' criteria tooltip);</li>
 *   <li>with {@code "arrays": true}, every text array the methods read from a static field is looked up
 *       too ({@link Texts#array}), for texts a mod keeps in an array it fills before the languages load
 *       (Head Tracker's compass letters);</li>
 *   <li>{@code "calls"} lists methods ({@code owner/Class.name}) whose text result is looked up too
 *       ({@link Texts#literal}), for a setting the pack fills with a text (MiniHUD's date format);</li>
 *   <li>{@code "methods"} limits a group to methods whose name contains that word, for code another mod's
 *       mixin put into a Minecraft class (Head Tracker's compass in LocatorBar).</li>
 * </ul>
 * The classes are reached through an empty mixin ({@code mixin.texts.HardcodedTextsMixin}, which lists them
 * again) so that this runs in {@code MixinPlugin.postApply}. Texts that are not in the list are left alone,
 * and a mod update that changes a text only means that text stays in the mod's own words.
 */
public final class TextPatcher {
    static final String FILE = "vanilla-finest-fixes.texts.json";
    private static final Logger LOG = LoggerFactory.getLogger("Vanilla Finest Fixes");
    private static final String TEXTS = "io/github/hiveko/vanillafinest/fixes/texts/Texts";
    private static final String STRING = "Ljava/lang/String;";
    private static final String COMPONENT = "net/minecraft/network/chat/Component";

    /** One group of {@value #FILE}. {@code texts}: the mod's text → full translation key. */
    record Group(String id, String mod, String methods, boolean literal, boolean arrays, Set<String> calls,
                 Map<String, String> texts) {}

    private static Map<String, List<Group>> byClass;
    private static Map<String, Group> byId;

    private TextPatcher() {}

    static synchronized void load() {
        if (byClass != null) return;
        byClass = new HashMap<>();
        byId = new HashMap<>();
        try (InputStream in = TextPatcher.class.getClassLoader().getResourceAsStream(FILE)) {
            if (in == null) return; // no hard-coded texts for this Minecraft version
            JsonObject groups = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject().getAsJsonObject("groups");
            for (Map.Entry<String, JsonElement> e : groups.entrySet()) {
                JsonObject o = e.getValue().getAsJsonObject();
                Map<String, String> texts = new LinkedHashMap<>();
                for (Map.Entry<String, JsonElement> t : o.getAsJsonObject("texts").entrySet()) {
                    texts.put(t.getKey(), "vanilla-finest-fixes." + e.getKey() + "." + t.getValue().getAsString());
                }
                Group g = new Group(e.getKey(), o.get("mod").getAsString(),
                        o.has("methods") ? o.get("methods").getAsString() : null,
                        flag(o, "literal"), flag(o, "arrays"), strings(o, "calls"), texts);
                byId.put(g.id(), g);
                for (JsonElement c : o.getAsJsonArray("classes")) {
                    byClass.computeIfAbsent(c.getAsString(), k -> new ArrayList<>()).add(g);
                }
            }
        } catch (Exception e) {
            LOG.error("Could not read {}; hard-coded texts stay as the mods wrote them", FILE, e);
        }
    }

    private static boolean flag(JsonObject o, String name) {
        return o.has(name) && o.get(name).getAsBoolean();
    }

    private static Set<String> strings(JsonObject o, String name) {
        Set<String> out = new HashSet<>();
        if (o.has(name)) for (JsonElement e : o.getAsJsonArray(name)) out.add(e.getAsString());
        return out;
    }

    /** The groups for this class whose mod is installed. */
    private static List<Group> groupsFor(String className) {
        load();
        List<Group> found = new ArrayList<>();
        for (Group g : byClass.getOrDefault(className, List.of())) {
            if (FabricLoader.getInstance().isModLoaded(g.mod())) found.add(g);
        }
        return found;
    }

    /** For the mixin plugin: true when this class has texts of an installed mod. */
    public static boolean wanted(String className) {
        return !groupsFor(className).isEmpty();
    }

    /** The translation key of a text made at run time, or null (for {@link Texts#literal} and {@link Texts#array}). */
    static String dynamicKey(String groupId, String text) {
        load();
        Group g = byId.get(groupId);
        return g == null ? null : g.texts().get(text);
    }

    /** The groups that list this class, installed or not. */
    static List<Group> declaredGroups(String className) {
        load();
        return byClass.getOrDefault(className, List.of());
    }

    /** Puts the translation calls into the class (see the class comment). */
    public static void patch(String className, ClassNode node) {
        int count = apply(node, groupsFor(className));
        if (count > 0) LOG.info("Hard-coded texts: {} places made translatable in {}", count, className);
    }

    /** Patches the class for these groups; returns the number of places changed. */
    static int apply(ClassNode node, List<Group> groups) {
        int count = 0;
        for (Group g : groups) {
            for (MethodNode m : node.methods) {
                if (g.methods() != null && !m.name.contains(g.methods())) continue;
                int before = count;
                for (AbstractInsnNode insn : m.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode ldc && ldc.cst instanceof String text) {
                        String key = g.texts().get(text);
                        if (key == null) continue;
                        InsnList add = new InsnList();
                        add.add(new LdcInsnNode(key));
                        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTS, "tr", "(" + STRING + STRING + ")" + STRING, false));
                        m.instructions.insert(ldc, add);
                        count++;
                    } else if (insn instanceof InvokeDynamicInsnNode indy && isConcat(indy)) {
                        String recipe = (String) indy.bsmArgs[0];
                        String key = g.texts().get(recipe.replace("\u0001", "%s"));
                        if (key == null) continue;
                        InsnList add = new InsnList();
                        add.add(new LdcInsnNode(recipe));
                        add.add(new LdcInsnNode(key));
                        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTS, "concat", "(" + STRING + STRING + STRING + ")" + STRING, false));
                        m.instructions.insert(indy, add);
                        count++;
                    } else if (g.literal() && insn instanceof MethodInsnNode call && call.getOpcode() == Opcodes.INVOKESTATIC
                            && call.owner.equals(COMPONENT) && call.name.equals("literal")
                            && call.desc.startsWith("(" + STRING + ")")) {
                        InsnList add = new InsnList();
                        add.add(new LdcInsnNode(g.id()));
                        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTS, "literal", "(" + STRING + STRING + ")" + STRING, false));
                        m.instructions.insertBefore(call, add);
                        count++;
                    } else if (!g.calls().isEmpty() && insn instanceof MethodInsnNode call
                            && call.desc.endsWith(")" + STRING) && g.calls().contains(call.owner + "." + call.name)) {
                        InsnList add = new InsnList();
                        add.add(new LdcInsnNode(g.id()));
                        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTS, "literal", "(" + STRING + STRING + ")" + STRING, false));
                        m.instructions.insert(call, add);
                        count++;
                    } else if (g.arrays() && insn instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETSTATIC
                            && field.desc.equals("[" + STRING)) {
                        InsnList add = new InsnList();
                        add.add(new LdcInsnNode(g.id()));
                        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTS, "array", "([" + STRING + STRING + ")[" + STRING, false));
                        m.instructions.insert(field, add);
                        count++;
                    }
                }
                if (count > before) m.maxStack += 3;
            }
        }
        return count;
    }

    /** A Java string concatenation whose recipe has no constant parts (only text and \u0001 values). */
    private static boolean isConcat(InvokeDynamicInsnNode indy) {
        return indy.name.equals("makeConcatWithConstants")
                && indy.bsm.getOwner().equals("java/lang/invoke/StringConcatFactory")
                && indy.bsmArgs.length == 1 && indy.bsmArgs[0] instanceof String recipe
                && recipe.indexOf('\u0002') < 0;
    }
}
