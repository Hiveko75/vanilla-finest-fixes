package io.github.hiveko.vanillafinest.fixes.mixin.betterclouds;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Matrix4f;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Better Clouds 1.14.x (Minecraft 26.2): clouds use Voxy's projection again, so they are hidden behind Voxy's
 * distant terrain while a shader pack is on.
 *
 * Every frame Better Clouds uploads the projection it uses to place clouds against the distant-terrain depth:
 * Distant Horizons' one if {@code DhCompat.getProjectionMatrix()} is not null, otherwise Voxy's. Without
 * Distant Horizons, DhCompat is a stub that returns a placeholder matrix instead of null, so Voxy's projection
 * was never uploaded and the depth test against Voxy's terrain never hid a cloud (with or without
 * {@link VoxyCloudDepthRangeMixin}). Here the stub's matrix is ignored when Distant Horizons is not loaded.
 * Without a shader pack Better Clouds does not use the distant-terrain test at all (LOD_ENABLED is off), so
 * nothing changes there. Better Clouds 1.15 (26.3) asks Voxy directly. Targeted by name; "betterclouds" module.
 */
@Mixin(targets = "com.qendolin.betterclouds.rendering.blaze3d.Blaze3DRenderer", remap = false)
public abstract class VoxyCloudProjectionMixin {
    @Unique
    private static final boolean vanillaFinest$distantHorizons = FabricLoader.getInstance().isModLoaded("distanthorizons");
    @Unique
    private static boolean vanillaFinest$logged;

    @ModifyExpressionValue(method = "render",
            at = @At(value = "INVOKE", target = "Lcom/qendolin/betterclouds/compat/DhCompat;getProjectionMatrix()Lorg/joml/Matrix4f;"))
    private Matrix4f vanillaFinest$useVoxyProjection(Matrix4f dhProjection) {
        if (vanillaFinest$distantHorizons) return dhProjection;
        if (!vanillaFinest$logged && dhProjection != null) {
            vanillaFinest$logged = true;
            LoggerFactory.getLogger("Vanilla Finest Fixes")
                    .info("Better Clouds: using Voxy's projection for the distant terrain test");
        }
        return null;
    }
}
