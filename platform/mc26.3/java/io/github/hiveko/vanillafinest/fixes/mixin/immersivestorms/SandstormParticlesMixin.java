package io.github.hiveko.vanillafinest.fixes.mixin.immersivestorms;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Smaller sandstorm grains. Immersive Storms 1.8.0 spawns its sandstorm dust with a size of 10, which
 * Minecraft's dust particles cap at 4 (the largest dust there is); this makes them {@value #SCALE}
 * (redstone dust is 1). A grain's quad is 2 × 0.75 × size × (0.1 to 0.2, random) blocks wide, and its
 * textures (dust_grain/var0-2, 8×8) hold a 1-pixel dot that grows to 2 and then 3×4 pixels with age. At
 * 2.2 the first dot is about one block pixel (1/16 block) on average, 0.7 to 1.3 of one with the random
 * size (Hiveko, 27 Nov; 1.5 made it about half a pixel). The ambient wind particles are not changed.
 * Immersive Storms module (26.3, 1.8.0 only; see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin});
 * targeted by name.
 */
@Mixin(targets = "com.thedeathlycow.immersive.storms.world.SandstormParticles", remap = false)
public abstract class SandstormParticlesMixin {
    private static final float SCALE = 2.2f;

    @ModifyArg(method = "onEndTick",
            at = @At(value = "INVOKE",
                    target = "Lcom/thedeathlycow/immersive/storms/particle/DustGrainParticleEffect;<init>(Lorg/joml/Vector3fc;F)V",
                    remap = false),
            index = 1, remap = false)
    private float vanillaFinest$smallerGrains(float scale) {
        return Math.min(scale, SCALE);
    }
}
