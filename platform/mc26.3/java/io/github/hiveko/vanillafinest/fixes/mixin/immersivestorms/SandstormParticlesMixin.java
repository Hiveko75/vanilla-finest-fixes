package io.github.hiveko.vanillafinest.fixes.mixin.immersivestorms;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Smaller sandstorm grains. Immersive Storms 1.8.0 spawns its sandstorm dust with a size of 10, which
 * Minecraft's dust particles cap at 4 (the largest dust there is); this makes them {@value #SCALE}
 * (redstone dust is 1). The ambient wind particles are not changed. Immersive Storms module (26.3,
 * 1.8.0 only; see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}); targeted by name.
 */
@Mixin(targets = "com.thedeathlycow.immersive.storms.world.SandstormParticles", remap = false)
public abstract class SandstormParticlesMixin {
    private static final float SCALE = 1.5f;

    @ModifyArg(method = "onEndTick",
            at = @At(value = "INVOKE",
                    target = "Lcom/thedeathlycow/immersive/storms/particle/DustGrainParticleEffect;<init>(Lorg/joml/Vector3fc;F)V",
                    remap = false),
            index = 1, remap = false)
    private float vanillaFinest$smallerGrains(float scale) {
        return Math.min(scale, SCALE);
    }
}
