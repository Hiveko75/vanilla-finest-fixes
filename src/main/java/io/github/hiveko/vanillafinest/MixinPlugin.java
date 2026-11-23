package io.github.hiveko.vanillafinest;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Applies each module's mixins only when the mod it belongs to is installed in a version the module was
 * made for ({@link ModuleCheck}): mixin.iris.* needs Iris (GlintTexturesMixin: the Iris version with the
 * glint bug), mixin.voxy.* Voxy, mixin.elytratrails.* Elytra Contrails, mixin.touchnmotion.* Touch'n
 * Motion, mixin.atmospherics.* Atmospherics, and mixin.folders.* Flashback (not with Flashback Settings)
 * and Axiom. Otherwise they are skipped, so nothing fails at startup.
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
        String key = moduleKey(mixinClassName);
        if ("flashback".equals(key) && FabricLoader.getInstance().isModLoaded("flashback-settings")) {
            return false; // Flashback Settings sets the replay folder itself
        }
        return key == null || ModuleCheck.matches(key);
    }

    /** The {@link ModuleCheck} key a mixin belongs to, or null for mixins that always apply. */
    static String moduleKey(String mixinClassName) {
        if (mixinClassName.endsWith(".mixin.iris.GlintTexturesMixin")) return "iris.glint";
        if (mixinClassName.contains(".mixin.iris.")) return "iris";
        if (mixinClassName.contains(".mixin.elytratrails.")) return "elytratrails";
        if (mixinClassName.contains(".mixin.touchnmotion.")) return "touch_n_motion";
        if (mixinClassName.contains(".mixin.voxy.")) return "voxy";
        if (mixinClassName.contains(".mixin.atmospherics.")) return "atmospherics";
        if (mixinClassName.endsWith(".mixin.folders.FlashbackMixin")) return "flashback";
        if (mixinClassName.endsWith(".mixin.folders.AxiomMixin")) return "axiom";
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
    }
}
