package io.github.hiveko.vanillafinest.fixes.mixin.headbrowser;

import io.github.hiveko.vanillafinest.fixes.headbrowser.CreativeOnly;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In a world the browser only opens in creative: the browse key (and anything else that opens it) does
 * nothing in survival or adventure. On the title screen it opens as before. Head Browser module
 * ({@link CreativeOnly}).
 */
@Mixin(targets = "io.github.thevoidblock.headbrowser.gui.BrowseScreen", remap = false)
public abstract class BrowseScreenMixin {
    @Inject(method = "open", at = @At("HEAD"), cancellable = true, remap = false)
    private static void vanillaFinest$creativeOnly(Minecraft client, CallbackInfo ci) {
        if (!CreativeOnly.usableHere()) ci.cancel();
    }
}
