package io.github.hiveko.vanillafinest.fixes.mixin.headbrowser;

import io.github.hiveko.vanillafinest.fixes.headbrowser.CreativeOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Taking a head needs creative, also in singleplayer (Head Browser allows it there in every game mode).
 * Head Browser module ({@link CreativeOnly}); targeted by name, nothing of Head Browser is compiled in.
 */
@Mixin(targets = "io.github.thevoidblock.headbrowser.HeadBrowser", remap = false)
public abstract class HeadBrowserMixin {
    @Inject(method = "canGetHeadItem()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void vanillaFinest$creativeOnly(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(CreativeOnly.inCreative());
    }
}
