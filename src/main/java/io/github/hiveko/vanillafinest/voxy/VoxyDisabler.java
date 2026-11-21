package io.github.hiveko.vanillafinest.voxy;

import io.github.hiveko.vanillafinest.VanillaFinest;
import org.slf4j.Logger;

/**
 * Voxy only renders with OpenGL, so it forces Minecraft onto OpenGL even when Vulkan is selected.
 * This keeps the selected Vulkan graphics API and switches Voxy off for that session instead
 * (mixin.voxy, 26.2 and 26.3). It does not make Voxy work on Vulkan; on OpenGL nothing changes.
 */
public final class VoxyDisabler {
    public static final Logger LOGGER = VanillaFinest.LOGGER;

    /** Exact message Voxy uses when it refuses to create a Vulkan device. */
    public static final String VOXY_REFUSAL_MESSAGE = "Voxy does not presently support vulkan";

    /** True once Minecraft has created its GPU device with a non-OpenGL backend (Vulkan). */
    public static volatile boolean nonOpenGlBackendActive = false;
    public static volatile String activeBackendName = "unknown";

    private VoxyDisabler() {}

    /** Was this exception thrown by Voxy's "force OpenGL" wrapper (and not by a real Vulkan failure)? */
    public static boolean isVoxyRefusal(Throwable t) {
        if (VOXY_REFUSAL_MESSAGE.equals(t.getMessage())) return true;
        for (StackTraceElement e : t.getStackTrace()) {
            if (e.getMethodName().contains("voxy$forceOpenglDevice")) return true;
        }
        return false;
    }
}
