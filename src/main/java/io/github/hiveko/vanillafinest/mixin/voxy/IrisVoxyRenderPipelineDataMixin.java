package io.github.hiveko.vanillafinest.mixin.voxy;

import io.github.hiveko.vanillafinest.voxy.MissingUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adds defaults for uniforms Iris does not provide to the shader pack's Voxy patch (see {@link MissingUniforms}). */
@Mixin(targets = "me.cortex.voxy.client.iris.IrisVoxyRenderPipelineData", remap = false)
public class IrisVoxyRenderPipelineDataMixin {
    @Inject(method = {"opaqueFragPatch", "translucentFragPatch"}, at = @At("RETURN"), cancellable = true, remap = false)
    private void vanillaFinest$declareMissingUniforms(CallbackInfoReturnable<String> cir) {
        String patched = MissingUniforms.patch(this, cir.getReturnValue());
        if (patched != null) cir.setReturnValue(patched);
    }
}
