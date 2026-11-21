package io.github.hiveko.vanillafinest.iris;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

/** Minecraft 1.21.11 and 26.x: Iris switches the "Improved Transparency" option off. */
final class TransparencyOption {
    static final String NAME = "Improved Transparency";

    private TransparencyOption() {}

    static OptionInstance<?> option(Options options) {
        return options.improvedTransparency();
    }

    /** Iris' change: on -> off. */
    static boolean isIrisDowngrade(Object oldValue, Object newValue) {
        return Boolean.TRUE.equals(oldValue) && Boolean.FALSE.equals(newValue);
    }

    /** Only switched back on if it is still off. */
    static boolean shouldRestore(Object current) {
        return Boolean.FALSE.equals(current);
    }

    static Object restoredValue() {
        return Boolean.TRUE;
    }
}
