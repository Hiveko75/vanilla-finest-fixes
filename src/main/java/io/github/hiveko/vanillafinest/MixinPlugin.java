package io.github.hiveko.vanillafinest;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Applies each mixin only when the mod it belongs to is installed: mixins in mixin.iris.* need Iris,
 * mixins in mixin.voxy.* need Voxy. Without that mod they are skipped, so nothing fails at startup.
 * Other mixins (mixin.particles) are always applied.
 */
public class MixinPlugin implements IMixinConfigPlugin {
    /** Set when this jar contains the Voxy part (26.2 and 26.3). */
    static boolean HAS_VOXY_PART;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".mixin.iris.")) {
            return FabricLoader.getInstance().isModLoaded("iris");
        }
        if (mixinClassName.contains(".mixin.voxy.")) {
            HAS_VOXY_PART = true;
            return FabricLoader.getInstance().isModLoaded("voxy");
        }
        return true;
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
