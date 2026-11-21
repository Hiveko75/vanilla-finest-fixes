package io.github.hiveko.vanillafinest.elytratrails;

import dbrighthd.elytratrails.api.ElytraTrailsAPI;
import dbrighthd.elytratrails.config.pack.ResolvedTrailSettings;
import dbrighthd.elytratrails.config.pack.TrailPackConfigManager;
import dbrighthd.elytratrails.rendering.EntityTrailGroup;
import dbrighthd.elytratrails.rendering.Trail;
import dbrighthd.elytratrails.rendering.WingTipSampler;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Elytra Contrails samples every flying player and every entity with a trail config up to 60 times
 * per second, rendering its model each time to find the trail emitters, even when all of that
 * entity's trails are switched off ("Enable Trail" off, for example "Enable Other's Trails"). A
 * switched-off trail only gets a lifetime of 0 when it is drawn, so nothing is shown.
 *
 * <p>This skips sampling for such entities:
 * <ul>
 *   <li>Players whose trail settings have "Enable Trail" off are not sampled at all, unless a
 *       resource pack has trail configs for elytra or player models, or another mod uses the
 *       Elytra Contrails API: those could switch single trails back on.</li>
 *   <li>Otherwise, when all trails Elytra Contrails created for an entity turn out to be off, that
 *       entity is skipped and checked again after a second, so a change of settings applies
 *       within a second.</li>
 * </ul>
 * Entities with at least one trail on are sampled exactly as before.
 *
 * <p>Player model trail spawners: with "EMF Model Support" on, every sample of a flying player also
 * renders and animates the whole player model a second time, only to look for EMF trail spawner bones
 * in it. When the player model has none, that always comes back empty. After it came back empty for a
 * player, the next {@value #MODEL_RECHECK_MS} ms skip it (same empty result), then it is checked again,
 * so a model or pack change is picked up within that time.
 */
public final class TrailSkip {
    private static final long RECHECK_MS = 1000;
    private static final long MODEL_RECHECK_MS = 5000;
    private static final WingTipSampler.EntityEmitters NO_EMITTERS = new WingTipSampler.EntityEmitters(List.of(), false);

    private static final Int2LongOpenHashMap DISABLED_UNTIL = new Int2LongOpenHashMap();
    private static final Int2LongOpenHashMap NO_MODEL_SPAWNERS_UNTIL = new Int2LongOpenHashMap();
    private static long packCheckedAt = -RECHECK_MS;
    private static boolean packMayEnablePlayerTrails;

    private TrailSkip() {}

    /** Player trail sampling: true to skip this player. */
    public static boolean skipPlayer(ResolvedTrailSettings settings, Entity player) {
        if (!settings.enableTrail() && !packMayEnablePlayerTrails()) {
            return true;
        }
        return skipEntity(player);
    }

    /** True while the entity's trails were all off at the last check. */
    public static boolean skipEntity(Entity entity) {
        long until = DISABLED_UNTIL.get(entity.getId());
        if (until == 0) return false;
        if (Util.getMillis() < until) return true;
        DISABLED_UNTIL.remove(entity.getId());
        return false;
    }

    /** Called with the entity's trail group each time Elytra Contrails samples it. */
    public static void observe(int entityId, Object group) {
        if (!(group instanceof EntityTrailGroup trailGroup) || trailGroup.trails().isEmpty()) return;
        for (Trail trail : trailGroup.trails()) {
            if (trail.config() == null || trail.config().enableTrail()) {
                DISABLED_UNTIL.remove(entityId);
                return;
            }
        }
        long now = Util.getMillis();
        if (DISABLED_UNTIL.size() > 512) {
            DISABLED_UNTIL.int2LongEntrySet().removeIf(e -> e.getLongValue() <= now);
        }
        DISABLED_UNTIL.put(entityId, now + RECHECK_MS);
    }

    private static boolean packMayEnablePlayerTrails() {
        long now = Util.getMillis();
        if (now - packCheckedAt >= RECHECK_MS) {
            packCheckedAt = now;
            boolean may = ElytraTrailsAPI.doAPIOverridesExist();
            if (!may) {
                for (String model : TrailPackConfigManager.getModelStrings()) {
                    if (model.contains("elytra") || model.contains("player")) {
                        may = true;
                        break;
                    }
                }
            }
            packMayEnablePlayerTrails = may;
        }
        return packMayEnablePlayerTrails;
    }

    /** The empty result to use while this player's model is known to have no trail spawners, else null. */
    public static WingTipSampler.EntityEmitters skipPlayerModel(Entity player) {
        long until = NO_MODEL_SPAWNERS_UNTIL.get(player.getId());
        if (until == 0) return null;
        if (Util.getMillis() < until) return NO_EMITTERS;
        NO_MODEL_SPAWNERS_UNTIL.remove(player.getId());
        return null;
    }

    /** Called with the result of the player model spawner search. */
    public static void observePlayerModel(Entity player, WingTipSampler.EntityEmitters result) {
        if (result == null || result.changedModelVariant() || !result.emitters().isEmpty()) {
            NO_MODEL_SPAWNERS_UNTIL.remove(player.getId());
            return;
        }
        long now = Util.getMillis();
        if (NO_MODEL_SPAWNERS_UNTIL.size() > 256) {
            NO_MODEL_SPAWNERS_UNTIL.int2LongEntrySet().removeIf(e -> e.getLongValue() <= now);
        }
        NO_MODEL_SPAWNERS_UNTIL.put(player.getId(), now + MODEL_RECHECK_MS);
    }
}
