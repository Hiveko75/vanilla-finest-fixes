package io.github.hiveko.vanillafinest.mixin.touchnmotion;

import io.github.hiveko.vanillafinest.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Touch'n Motion 1.0.5 turns a seated player's head (on a horse, in a minecart, at a fence, in a
 * cockpit) towards ShoulderCamera.worldView. For the local player that is the camera's forward
 * direction. With a third-person camera that moves around the player freely, like Leawind's Third
 * Person, the head turned to where the camera points instead of where the player faces, and it looked
 * wrong with shoulder cameras too. 1.0.4 left the riding head to Minecraft, which uses the player's own
 * facing.
 *
 * <p>This returns the player's own view direction, like for every other player, with every camera mod
 * (Shoulder Surfing included). The class is targeted by name, so nothing of Touch'n Motion is compiled in.
 */
@Mixin(targets = "strm.touchnmotion.compat.ShoulderCamera")
public abstract class ShoulderCameraMixin {
    @Inject(method = "worldView(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"), cancellable = true)
    private static void vanillaFinest$playerFacing(AbstractClientPlayer player, CallbackInfoReturnable<Vec3> cir) {
        cir.setReturnValue(player.getViewVector(Platform.partialTick(Minecraft.getInstance())));
    }
}
