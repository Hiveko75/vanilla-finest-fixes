package io.github.hiveko.vanillafinest.iris;

import java.lang.reflect.Method;

import io.github.hiveko.vanillafinest.HelperSettings;
import io.github.hiveko.vanillafinest.VanillaFinest;
import io.github.hiveko.vanillafinest.mixin.iris.OptionInstanceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

/**
 * Iris switches Minecraft's better transparency off whenever a shader pack is enabled (Improved
 * Transparency on 1.21.11 and 26.x, Fabulous graphics on 1.21.1; Iris' MixinDisableFabulousGraphics,
 * on every renderer reload), but never switches it back on when the shader pack is disabled again.
 * This remembers that Iris switched it off and switches it back on at the next renderer reload
 * without shaders (Iris starts one itself when shaders are disabled).
 *
 * <p>Only a change made while shaders are enabled is remembered: the option is locked then, so that
 * is Iris. If it was already off before shaders were enabled, nothing is remembered or changed. The
 * flag is kept in config/vanilla-finest-helper.properties, so it also works after a restart with shaders on.
 * The version-specific option is in {@link TransparencyOption} (platform/).
 */
public final class TransparencyRestore {
    private static final String KEY = "restoreTransparency";

    private static Boolean restore;          // loaded from the file on first use
    private static boolean irisLookedUp;
    private static Method getIrisConfig;
    private static Method areShadersEnabled;

    private TransparencyRestore() {
    }

    /** Called before any OptionInstance.set. */
    public static void beforeSet(OptionInstance<?> option, Object newValue) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || option != TransparencyOption.option(mc.options)) {
            return;
        }
        if (TransparencyOption.isIrisDowngrade(option.get(), newValue) && shadersEnabled() && !isRestore()) {
            setRestore(true);
            VanillaFinest.LOGGER.info("Iris turned {} off for shaders; it will be turned back on when shaders are disabled",
                    TransparencyOption.NAME);
        }
    }

    /** Called at the start of a renderer reload (the same places where Iris switches it off). */
    public static void beforeRendererReload() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || !isRestore() || shadersEnabled()) {
            return;
        }
        setRestore(false);
        OptionInstance<?> option = TransparencyOption.option(mc.options);
        if (TransparencyOption.shouldRestore(option.get())) {
            // Set the value directly: the option's own listener would start another renderer reload,
            // and the one that is just starting already uses the new value.
            ((OptionInstanceAccessor) (Object) option).vanillaFinest$setValue(TransparencyOption.restoredValue());
            mc.options.save();
            VanillaFinest.LOGGER.info("Shaders disabled: {} turned back on", TransparencyOption.NAME);
        }
    }

    private static boolean shadersEnabled() {
        if (!irisLookedUp) {
            irisLookedUp = true;
            try {
                Class<?> iris = Class.forName("net.irisshaders.iris.Iris");
                getIrisConfig = iris.getMethod("getIrisConfig");
                areShadersEnabled = getIrisConfig.getReturnType().getMethod("areShadersEnabled");
            } catch (ReflectiveOperationException | LinkageError e) {
                VanillaFinest.LOGGER.warn("Iris config not found; transparency will not be restored", e);
            }
        }
        if (areShadersEnabled == null) {
            return false;
        }
        try {
            Object config = getIrisConfig.invoke(null);
            return config != null && (Boolean) areShadersEnabled.invoke(config);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }

    private static boolean isRestore() {
        if (restore == null) {
            restore = Boolean.parseBoolean(HelperSettings.get(KEY, "false"));
        }
        return restore;
    }

    private static void setRestore(boolean value) {
        restore = value;
        HelperSettings.set(KEY, Boolean.toString(value));
    }
}
