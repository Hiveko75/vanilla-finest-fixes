package io.github.hiveko.vanillafinest.elytratrails;

import dbrighthd.elytratrails.rendering.WingTipSampler;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Player model trail spawners (Elytra Contrails 1.4.9 and later): with "EMF Model Support" on, every
 * sample of a flying player also renders and animates the whole player model a second time, only to
 * look for EMF trail spawner bones in it. When the player model has none, that always comes back
 * empty. After it came back empty for a player, the next {@value #MODEL_RECHECK_MS} ms skip it (same
 * empty result), then it is checked again, so a model or pack change is picked up within that time.
 */
public final class PlayerModelSkip {
    private static final long MODEL_RECHECK_MS = 5000;
    private static final WingTipSampler.EntityEmitters NO_EMITTERS = new WingTipSampler.EntityEmitters(List.of(), false);
    private static final Int2LongOpenHashMap NO_MODEL_SPAWNERS_UNTIL = new Int2LongOpenHashMap();

    private PlayerModelSkip() {}

    /** The empty result to use while this player's model is known to have no trail spawners, else null. */
    public static WingTipSampler.EntityEmitters skip(Entity player) {
        long until = NO_MODEL_SPAWNERS_UNTIL.get(player.getId());
        if (until == 0) return null;
        if (TrailSkip.now() < until) return NO_EMITTERS;
        NO_MODEL_SPAWNERS_UNTIL.remove(player.getId());
        return null;
    }

    /** Called with the result of the player model spawner search. */
    public static void observe(Entity player, WingTipSampler.EntityEmitters result) {
        if (result == null || result.changedModelVariant() || !result.emitters().isEmpty()) {
            NO_MODEL_SPAWNERS_UNTIL.remove(player.getId());
            return;
        }
        long now = TrailSkip.now();
        if (NO_MODEL_SPAWNERS_UNTIL.size() > 256) {
            NO_MODEL_SPAWNERS_UNTIL.int2LongEntrySet().removeIf(e -> e.getLongValue() <= now);
        }
        NO_MODEL_SPAWNERS_UNTIL.put(player.getId(), now + MODEL_RECHECK_MS);
    }
}
