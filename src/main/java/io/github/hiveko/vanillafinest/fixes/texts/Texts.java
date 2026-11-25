package io.github.hiveko.vanillafinest.fixes.texts;

import net.minecraft.locale.Language;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The calls {@link TextPatcher} puts into other mods' code. Each one returns the mod's own text when the
 * loaded languages have no translation for it, so nothing is ever lost.
 */
public final class Texts {
    private static final Map<String, Pattern> RECIPES = new ConcurrentHashMap<>();
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:(\\d+)\\$)?s|%%");

    private Texts() {}

    /** A text constant: its translation, or the text itself. */
    public static String tr(String text, String key) {
        return Language.getInstance().getOrDefault(key, text);
    }

    /**
     * A text the mod built from {@code recipe} (Java's string concatenation recipe: text with \u0001 where
     * each value goes): the translation with the same values in place of %s or %1$s, %2$s.
     */
    public static String concat(String result, String recipe, String key) {
        String template = Language.getInstance().getOrDefault(key, null);
        if (template == null) return result;
        Matcher m = RECIPES.computeIfAbsent(recipe, Texts::compile).matcher(result);
        if (!m.matches()) return result;
        String[] values = new String[m.groupCount()];
        for (int i = 0; i < values.length; i++) values[i] = m.group(i + 1);
        return format(template, values);
    }

    /** A text given to Component.literal in a "literal" group: its translation if it is listed. */
    public static String literal(String text, String group) {
        if (text == null) return null;
        String key = TextPatcher.dynamicKey(group, text);
        return key == null ? text : Language.getInstance().getOrDefault(key, text);
    }

    /** A text array in an "arrays" group: a copy with the listed texts translated. */
    public static String[] array(String[] texts, String group) {
        if (texts == null) return null;
        String[] out = new String[texts.length];
        for (int i = 0; i < texts.length; i++) out[i] = literal(texts[i], group);
        return out;
    }

    private static Pattern compile(String recipe) {
        StringBuilder re = new StringBuilder();
        int start = 0;
        for (int i = recipe.indexOf('\u0001'); i >= 0; i = recipe.indexOf('\u0001', start)) {
            re.append(Pattern.quote(recipe.substring(start, i))).append("(.*?)");
            start = i + 1;
        }
        re.append(Pattern.quote(recipe.substring(start)));
        return Pattern.compile(re.toString(), Pattern.DOTALL);
    }

    /** Minecraft-style placeholders: %s in order, %1$s by number, %% for a percent sign. */
    static String format(String template, String[] values) {
        Matcher m = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        int next = 0;
        while (m.find()) {
            String value;
            if (m.group().equals("%%")) value = "%";
            else {
                int index = m.group(1) != null ? Integer.parseInt(m.group(1)) - 1 : next++;
                value = index >= 0 && index < values.length ? values[index] : "";
            }
            m.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        m.appendTail(out);
        return out.toString();
    }
}
