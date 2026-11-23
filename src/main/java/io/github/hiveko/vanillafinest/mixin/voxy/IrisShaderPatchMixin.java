package io.github.hiveko.vanillafinest.mixin.voxy;

import io.github.hiveko.vanillafinest.voxy.MissingUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Remembers the uniform list a shader pack's Voxy patch asks for (see {@link MissingUniforms}). */
@Mixin(targets = "me.cortex.voxy.client.iris.IrisShaderPatch", remap = false)
public class IrisShaderPatchMixin {
    @Inject(method = "getUniformList", at = @At("RETURN"), remap = false)
    private void vanillaFinest$rememberUniforms(CallbackInfoReturnable<String[]> cir) {
        MissingUniforms.requested = cir.getReturnValue();
    }
}
