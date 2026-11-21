package io.github.hiveko.vanillafinest.mixin.elytratrails;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dbrighthd.elytratrails.config.pack.ResolvedSampleSettings;
import dbrighthd.elytratrails.config.pack.ResolvedTrailSettings;
import dbrighthd.elytratrails.rendering.TrailManager;
import io.github.hiveko.vanillafinest.elytratrails.TrailSkip;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Elytra Contrails (1.6.x, 26.3) samples trails at the end of every level extraction, up to its
 * "max samples per second". These hooks:
 * <ul>
 *   <li>loop over the players only when looking for player trails (it looped over every entity being
 *       rendered and skipped everything that is not a player);</li>
 *   <li>skip players and entities whose trails are all switched off ({@link TrailSkip}). The skip
 *       goes through the mod's own "trail not valid" path, so their trails are stopped exactly as
 *       when they stop flying;</li>
 *   <li>look at each trail group the mod creates or reuses, to learn whether its trails are on.</li>
 * </ul>
 * The entity hooks list both the 1.6.10 and the 1.6.11 method that holds the entity loop; require = 0
 * there, so a later version that changes the loop only loses the skipping, not the game.
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

    @WrapOperation(method = {"gatherEntityTrails", "gatherEntityTrailsInIterable"},
            at = @At(value = "INVOKE", target = "Ldbrighthd/elytratrails/rendering/TrailManager;isEntityTrailValid(Ldbrighthd/elytratrails/config/pack/ResolvedSampleSettings;Lnet/minecraft/world/entity/Entity;)Z"),
            require = 0)
    private boolean vanillaFinest$skipEntitiesWithoutTrails(ResolvedSampleSettings settings, Entity entity, Operation<Boolean> original) {
        return original.call(settings, entity) && !TrailSkip.skipEntity(entity);
    }

    @WrapOperation(method = {"gatherPlayerTrails", "gatherEntityTrails", "gatherEntityTrailsInIterable"},
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;computeIfAbsent(ILit/unimi/dsi/fastutil/ints/Int2ObjectFunction;)Ljava/lang/Object;"),
            require = 0)
    private Object vanillaFinest$observeTrailGroup(Int2ObjectMap<?> map, int entityId, Int2ObjectFunction<?> create, Operation<Object> original) {
        Object group = original.call(map, entityId, create);
        TrailSkip.observe(entityId, group);
        return group;
    }
}
