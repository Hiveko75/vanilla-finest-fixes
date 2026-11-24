package io.github.hiveko.vanillafinest.fixes.iris;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

/**
 * Minecraft 1.21.1 has no separate Improved Transparency option: it is part of the "Fabulous!"
 * graphics mode, and Iris switches Fabulous! down to Fancy.
 */
final class TransparencyOption {
    static final String NAME = "Fabulous! graphics";

    private TransparencyOption() {}

    static OptionInstance<?> option(Options options) {
        return options.graphicsMode();
    }

    /** Iris' change: Fabulous! -> Fancy. */
    static boolean isIrisDowngrade(Object oldValue, Object newValue) {
        return oldValue == GraphicsStatus.FABULOUS && newValue == GraphicsStatus.FANCY;
    }

    /** Only switched back if it is still Fancy (not if Fast was chosen meanwhile). */
    static boolean shouldRestore(Object current) {
        return current == GraphicsStatus.FANCY;
    }

    static Object restoredValue() {
        return GraphicsStatus.FABULOUS;
    }
}
