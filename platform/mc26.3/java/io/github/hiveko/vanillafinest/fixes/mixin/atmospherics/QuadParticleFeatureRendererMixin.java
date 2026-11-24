package io.github.hiveko.vanillafinest.fixes.mixin.atmospherics;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.hiveko.vanillafinest.fixes.atmospherics.ParticleOitFallback;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.feature.QuadParticleFeatureRenderer;
import net.minecraft.client.renderer.oit.OitStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * getOitPipeline throws when a particle layer has no OIT pipelines. Both Minecraft's particle drawing
 * and Async Particles' GPU path call it, so this one hook covers both: such layers get the pipelines
 * from {@link ParticleOitFallback}. Layers that have their own are not touched. Atmospherics module
 * (26.3, Atmospherics 2.6.8 only; see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}).
 */
@Mixin(QuadParticleFeatureRenderer.class)
public abstract class QuadParticleFeatureRendererMixin {
    @Inject(method = "getOitPipeline", at = @At("HEAD"), cancellable = true)
    private static void vanillaFinest$missingOitPipelines(OitStage stage, SingleQuadParticle.Layer layer,
                                                          CallbackInfoReturnable<RenderPipeline> cir) {
        if (layer.oitPipelineSet() == null) {
            cir.setReturnValue(ParticleOitFallback.forLayer(layer).getPipeline(stage));
        }
    }
}
