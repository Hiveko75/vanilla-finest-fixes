package io.github.hiveko.vanillafinest.fixes;

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
 * Motion and mixin.atmospherics.* Atmospherics. Otherwise they are skipped, so nothing fails at startup.
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
        if (mixinClassName.contains(".mixin.voxy.Iris")) {
            org.slf4j.LoggerFactory.getLogger("Vanilla Finest Fixes").info("Voxy shader fix hooked into {}", targetClassName);
        }
    }
}
