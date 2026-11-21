package io.github.hiveko.vanillafinest.mixin.iris;

import io.github.hiveko.vanillafinest.iris.TransparencyRestore;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Switches better transparency back on at the start of a renderer reload once shaders are off: the
 * same two methods where this version's Iris switches it off.
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "allChanged", at = @At("HEAD"))
    private void vanillaFinest$restoreOnAllChanged(CallbackInfo ci) {
        TransparencyRestore.beforeRendererReload();
    }

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void vanillaFinest$restoreOnResourceReload(CallbackInfo ci) {
        TransparencyRestore.beforeRendererReload();
    }
}
