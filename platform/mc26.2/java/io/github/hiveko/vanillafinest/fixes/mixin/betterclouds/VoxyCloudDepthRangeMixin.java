package io.github.hiveko.vanillafinest.fixes.mixin.betterclouds;

import com.mojang.blaze3d.systems.RenderSystem;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Better Clouds 1.14.x (Minecraft 26.2): clouds are hidden behind Voxy's distant terrain again while a shader
 * pack is on.
 *
 * Better Clouds hides a cloud when its depth, worked out with Voxy's projection, lies behind the depth in
 * Voxy's depth texture. Its Z_NEG1_TO_1 switch says whether that projection gives depths from -1 to 1 (which
 * then have to be moved to 0..1 before the comparison), and 1.14 only turns it on for Distant Horizons' own
 * renderer. While a shader pack is on, Iris 1.11.2 makes Minecraft use the -1..1 range (DeviceInfo
 * isZZeroToOne() is false), and Voxy 0.2.19 takes its range from the same place, so Better Clouds compared
 * values on two different scales and drew clouds in front of distant mountains. Better Clouds 1.15 (26.3)
 * asks Voxy for this itself; here the switch is set the same way: on when Voxy's depth is used and the
 * device is not zero-to-one. Without shaders nothing changes. Targeted by name; "betterclouds" module.
 */
@Mixin(targets = "com.qendolin.betterclouds.rendering.blaze3d.PipelineParams", remap = false)
public abstract class VoxyCloudDepthRangeMixin {
    @Unique
    private static boolean vanillaFinest$logged;

    @ModifyArg(method = "newParameters",
            at = @At(value = "INVOKE", target = "Lcom/qendolin/betterclouds/rendering/blaze3d/PipelineParams;<init>(ZZZZZZZ)V"),
            index = 5)
    private static boolean vanillaFinest$voxyDepthRange(boolean celestialBodyHalo, boolean nearCloudFade, boolean iris,
                                                        boolean distantHorizons, boolean voxy, boolean zNeg1To1,
                                                        boolean cloudsOpaque) {
        if (zNeg1To1 || !voxy || distantHorizons) return zNeg1To1;
        if (RenderSystem.getDevice().getDeviceInfo().isZZeroToOne()) return false;
        if (!vanillaFinest$logged) {
            vanillaFinest$logged = true;
            LoggerFactory.getLogger("Vanilla Finest Fixes")
                    .info("Better Clouds: using Voxy's -1..1 depth range, so clouds stay behind Voxy terrain with shaders");
        }
        return true;
    }
}
