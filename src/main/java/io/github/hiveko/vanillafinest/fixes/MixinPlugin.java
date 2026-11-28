package io.github.hiveko.vanillafinest.fixes;

import java.util.List;
import java.util.Set;

import io.github.hiveko.vanillafinest.fixes.texts.TextPatcher;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Applies each module's mixins only when the mod it belongs to is installed in a version the module was
 * made for ({@link ModuleCheck}): mixin.iris.* needs Iris (GlintTexturesMixin: the Iris version with the
 * glint bug), mixin.voxy.* Voxy, mixin.elytratrails.* Elytra Contrails, mixin.touchnmotion.* Touch'n
 * Motion, mixin.atmospherics.* Atmospherics, mixin.eso.* Enchanted's Sodium Options, mixin.headbrowser.*
 * Head Browser, mixin.flashback.* Flashback (not with Flashback Settings), mixin.gnetum.* Gnetum and mixin.betterclouds.* Better Clouds (26.2: Better Clouds 1.14). Otherwise
 * they are
 * skipped, so nothing fails at startup.
 *
 * <p>mixin.texts.HardcodedTextsMixin is empty: it only brings the classes listed in
 * vanilla-finest-fixes.texts.json here, applied to those whose mod is installed, and {@link #postApply}
 * lets {@link TextPatcher} make their hard-coded texts translatable.
 */
public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".mixin.texts.")) return TextPatcher.wanted(targetClassName);
        String key = moduleKey(mixinClassName);
        if ("flashback".equals(key) && FabricLoader.getInstance().isModLoaded("flashback-settings")) {
            return false; // Flashback Settings moves the replay button itself
        }
        return key == null || ModuleCheck.matches(key);
    }

    /** The {@link ModuleCheck} key a mixin belongs to, or null for mixins that always apply. */
    static String moduleKey(String mixinClassName) {
        if (mixinClassName.endsWith(".mixin.iris.GlintTexturesMixin")) return "iris.glint";
        if (mixinClassName.endsWith(".mixin.iris.IrisLanguageMixin")) return "iris.lang";
        if (mixinClassName.contains(".mixin.iris.")) return "iris";
        if (mixinClassName.contains(".mixin.elytratrails.")) return "elytratrails";
        if (mixinClassName.contains(".mixin.touchnmotion.")) return "touch_n_motion";
        if (mixinClassName.contains(".mixin.voxy.")) return "voxy";
        if (mixinClassName.contains(".mixin.atmospherics.")) return "atmospherics";
        if (mixinClassName.contains(".mixin.eso.")) return "enchanteds_sodium_options";
        if (mixinClassName.contains(".mixin.headbrowser.")) return "headbrowser";
        if (mixinClassName.contains(".mixin.immersivestorms.")) return "immersive-storms";
        if (mixinClassName.contains(".mixin.gnetum.")) return "gnetum";
        if (mixinClassName.contains(".mixin.esm.")) return "extremesoundmuffler";
        if (mixinClassName.contains(".mixin.betterclouds.")) return "betterclouds";
        if (mixinClassName.contains(".mixin.flashback.")) return "flashback";
        if (mixinClassName.endsWith(".mixin.weather.SodiumWeatherRangeMixin")) return "sodium.weather";
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.contains(".mixin.texts.")) {
            TextPatcher.patch(targetClassName, targetClass);
        }
        if (mixinClassName.contains(".mixin.voxy.Iris")) {
            org.slf4j.LoggerFactory.getLogger("Vanilla Finest Fixes").info("Voxy shader fix hooked into {}", targetClassName);
        }
    }
}
