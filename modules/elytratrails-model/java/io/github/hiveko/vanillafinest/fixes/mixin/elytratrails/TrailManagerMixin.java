package io.github.hiveko.vanillafinest.fixes.mixin.elytratrails;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dbrighthd.elytratrails.config.pack.ResolvedSampleSettings;
import dbrighthd.elytratrails.config.pack.ResolvedTrailSettings;
import dbrighthd.elytratrails.rendering.TrailManager;
import dbrighthd.elytratrails.rendering.WingTipSampler;
import io.github.hiveko.vanillafinest.fixes.elytratrails.PlayerModelSkip;
import io.github.hiveko.vanillafinest.fixes.elytratrails.TrailSkip;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Elytra Contrails (1.4.9 to 1.6.x: 1.21.11, 26.1.2, 26.2 and 26.3) samples trails at the end of
 * every level extraction, up to its "max samples per second". These hooks:
 * <ul>
 *   <li>loop over the players only when looking for player trails (it looped over every entity being
 *       rendered and skipped everything that is not a player);</li>
 *   <li>skip players and entities whose trails are all switched off ({@link TrailSkip}). The skip
 *       goes through the mod's own "trail not valid" path, so their trails are stopped exactly as
 *       when they stop flying;</li>
 *   <li>look at each trail group the mod creates or reuses, to learn whether its trails are on;</li>
 *   <li>skip the second player-model render that looks for EMF trail spawner bones while the player's
 *       model is known to have none ({@link PlayerModelSkip}).</li>
 * </ul>
 * The entity hooks list both the 1.6.10 and the 1.6.11 method that holds the entity loop; require = 0
 * there, so a later version that changes the loop only loses the skipping, not the game. Elytra
 * Contrails for 1.21.1 (1.4.7) has no entity trails and no model spawner search: it has its own
 * smaller version of this mixin in platform/mc1.21.1.
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

    @WrapOperation(method = "gatherPlayerTrails",
            at = @At(value = "INVOKE", target = "Ldbrighthd/elytratrails/rendering/WingTipSampler;getEntityTrailEmitterPositions(Lnet/minecraft/world/entity/Entity;FLdbrighthd/elytratrails/config/pack/ResolvedSampleSettings;)Ldbrighthd/elytratrails/rendering/WingTipSampler$EntityEmitters;"))
    private WingTipSampler.EntityEmitters vanillaFinest$skipEmptyPlayerModel(WingTipSampler sampler, Entity player, float partialTick,
                                                                          ResolvedSampleSettings settings, Operation<WingTipSampler.EntityEmitters> original) {
        WingTipSampler.EntityEmitters skipped = PlayerModelSkip.skip(player);
        if (skipped != null) return skipped;
        WingTipSampler.EntityEmitters result = original.call(sampler, player, partialTick, settings);
        PlayerModelSkip.observe(player, result);
        return result;
    }
}
