package io.github.hiveko.vanillafinest.fixes.atmospherics;

import java.util.HashSet;
import java.util.Set;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.hiveko.vanillafinest.fixes.VanillaFinestFixes;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.resources.Identifier;

/**
 * Minecraft 26.3: with Improved Transparency on, translucent particles are drawn with order-independent
 * transparency, and every particle layer needs its own set of OIT pipelines. A mod that creates a layer
 * without them crashes the game ("OIT pipeline set for particle layer not specified"). Atmospherics
 * 2.6.8 does this for its AirHaze (ambient mist) particles.
 *
 * <p>For Atmospherics' mist this builds the matching OIT pipelines: Minecraft's own OIT particle
 * pipeline with Atmospherics' look (no alpha cutoff, no face culling) and additive blending
 * (OIT_ADDITIVE, the mode Minecraft uses for energy swirls and lightning), which matches the mist's
 * normal SRC_ALPHA, ONE blending. Any other layer without OIT pipelines gets Minecraft's translucent
 * particle ones, so it is drawn instead of crashing.
 *
 * <p>Atmospherics module: only applied with Atmospherics 2.6.8 (the version with the missing pipelines,
 * reported to its author), so a fixed Atmospherics version is left alone.
 */
public final class ParticleOitFallback {
    private static final Identifier ATMOSPHERICS_MIST =
            Identifier.fromNamespaceAndPath("atmospherics", "pipeline/ambient_mist_additive_translucent");
    private static final Set<Identifier> WARNED = new HashSet<>();
    private static OitPipelineSet mist;

    private ParticleOitFallback() {}

    public static synchronized OitPipelineSet forLayer(SingleQuadParticle.Layer layer) {
        Identifier location = layer.pipeline().getLocation();
        if (ATMOSPHERICS_MIST.equals(location)) {
            if (mist == null) {
                mist = RenderPipelines.register(OitPipelineSet.builder("vanilla_finest_fixes/atmospherics_mist",
                        RenderPipeline.builder(RenderPipelines.OIT_PARTICLE_SNIPPET)
                                .withFragmentShader(Identifier.fromNamespaceAndPath(VanillaFinestFixes.MOD_ID, "core/additive_particle"))
                                .withShaderDefine("OIT_ADDITIVE")
                                .withCull(false))
                        .build());
                VanillaFinestFixes.LOGGER.info("Atmospherics AirHaze: using additive Improved Transparency pipelines");
            }
            return mist;
        }
        if (WARNED.add(location)) {
            VanillaFinestFixes.LOGGER.warn("Particle layer {} has no Improved Transparency pipelines; using Minecraft's translucent particle ones", location);
        }
        return RenderPipelines.OIT_PARTICLE;
    }
}
