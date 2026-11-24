package io.github.hiveko.vanillafinest.fixes.mixin.elytratrails;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dbrighthd.elytratrails.config.pack.ResolvedTrailSettings;
import dbrighthd.elytratrails.rendering.TrailManager;
import io.github.hiveko.vanillafinest.fixes.elytratrails.TrailSkip;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Elytra Contrails for 1.21.1 (1.4.7) only has player trails, sampled at the end of every frame up to
 * its "max samples per second". These hooks:
 * <ul>
 *   <li>loop over the players only (it looped over every entity being rendered and skipped everything
 *       that is not a player);</li>
 *   <li>skip players whose trails are all switched off ({@link TrailSkip}), through the mod's own
 *       "trail not valid" path, so their trails are stopped exactly as when they stop flying;</li>
 *   <li>look at each trail group the mod creates or reuses, to learn whether its trails are on.</li>
 * </ul>
 * There are no entity trails and no EMF trail spawner search in this version, so the other hooks of
 * the newer versions are not needed here.
 */
@Mixin(TrailManager.class)
public abstract class TrailManagerMixin {
    @SuppressWarnings("unchecked")
    @WrapOperation(method = "gatherPlayerTrails",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private Iterable<Entity> vanillaFinest$playersOnly(ClientLevel level, Operation<Iterable<Entity>> original) {
        return (Iterable<Entity>) (Iterable<?>) level.players();
    }

    @WrapOperation(method = "gatherPlayerTrails",
            at = @At(value = "INVOKE", target = "Ldbrighthd/elytratrails/rendering/TrailManager;isPlayerTrailValid(Ldbrighthd/elytratrails/config/pack/ResolvedTrailSettings;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean vanillaFinest$skipPlayersWithoutTrails(ResolvedTrailSettings settings, Entity player, Operation<Boolean> original) {
        return original.call(settings, player) && !TrailSkip.skipPlayer(settings, player);
    }

    @WrapOperation(method = "gatherPlayerTrails",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;computeIfAbsent(ILit/unimi/dsi/fastutil/ints/Int2ObjectFunction;)Ljava/lang/Object;"),
            require = 0)
    private Object vanillaFinest$observeTrailGroup(Int2ObjectMap<?> map, int entityId, Int2ObjectFunction<?> create, Operation<Object> original) {
        Object group = original.call(map, entityId, create);
        TrailSkip.observe(entityId, group);
        return group;
    }
}
