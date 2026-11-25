package io.github.hiveko.vanillafinest.fixes.mixin.headbrowser;

import io.github.hiveko.vanillafinest.fixes.headbrowser.CreativeOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * No Head Browser button on the pause screen outside creative: Head Browser only adds it when its
 * "Pause button" option is on, so in a world that is not in creative the option reads as off. The setting
 * itself is not changed. Head Browser module ({@link CreativeOnly}).
 */
@Mixin(targets = "io.github.thevoidblock.headbrowser.HeadBrowserConfig", remap = false)
public abstract class HeadBrowserConfigMixin {
    @Inject(method = "pauseButton()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void vanillaFinest$creativeOnly(CallbackInfoReturnable<Boolean> cir) {
        if (!CreativeOnly.usableHere()) cir.setReturnValue(false);
    }
}
