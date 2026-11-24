package io.github.hiveko.vanillafinest.fixes.imageio;

import javax.imageio.ImageIO;

/**
 * ImageIO module: Java's ImageIO buffers images it reads from a stream in a temporary file by default.
 * Mods that read textures that way on the render thread, like Xaero's World Map computing the colour
 * of a block it has not seen yet, then create and delete a temp file on disk for every texture, which
 * showed up in spark as frame hitches. With the cache off, ImageIO buffers in memory instead (block
 * textures are tiny); nothing else changes.
 *
 * <p>Not tied to one mod: any mod reading images through ImageIO benefits, so it is always on.
 */
public final class ImageIOCache {
    private ImageIOCache() {}

    /** Turns the disk cache off; false if that failed. */
    public static boolean init() {
        try {
            ImageIO.setUseCache(false);
            return true;
        } catch (Throwable t) {
            io.github.hiveko.vanillafinest.fixes.VanillaFinestFixes.LOGGER.warn("Could not turn off the ImageIO disk cache; the ImageIO module is off", t);
            return false;
        }
    }
}
