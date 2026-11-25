package io.github.hiveko.vanillafinest.fixes.headbrowser;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;

/**
 * Head Browser module: the head browser belongs to creative. Head Browser itself lets a player take any head
 * in a singleplayer world in every game mode (and on servers in creative), which is a free source of heads in
 * survival. With this module heads can only be taken in creative, and in a world the browser (pause-screen
 * button, key) is only offered in creative; outside a world (title screen) it stays available.
 * For the original Head Browser 2.6.x; the packs' own 26.3 and 1.21.1 ports do the same themselves.
 */
public final class CreativeOnly {
    private CreativeOnly() {}

    /** True when the player is in a world in creative mode. */
    public static boolean inCreative() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.CREATIVE;
    }

    /** True outside a world (title screen) or in creative. */
    public static boolean usableHere() {
        return Minecraft.getInstance().player == null || inCreative();
    }
}
