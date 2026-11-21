package io.github.hiveko.vanillafinest;

import javax.imageio.ImageIO;

/**
 * Start-up settings for this Minecraft version (26.3).
 *
 * <p>ImageIO disk cache off: Java's ImageIO buffers images it reads from a stream in a temporary file by
 * default. Mods that read textures that way on the render thread, like Xaero's World Map computing the
 * colour of a block it has not seen yet, then create and delete a temp file on disk for every texture,
 * which showed up in spark as frame hitches. With the cache off, ImageIO buffers in memory instead
 * (block textures are tiny); nothing else changes.
 */
final class VersionInit {
    private VersionInit() {}

    static void init() {
        try {
            ImageIO.setUseCache(false);
            VanillaFinest.LOGGER.info("ImageIO disk cache turned off (images are buffered in memory)");
        } catch (Throwable t) {
            VanillaFinest.LOGGER.warn("Could not turn off the ImageIO disk cache", t);
        }
    }
}
