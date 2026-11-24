package io.github.hiveko.vanillafinest.fixes;

import io.github.hiveko.vanillafinest.fixes.clouds.CloudsToggle;
import io.github.hiveko.vanillafinest.fixes.imageio.ImageIOCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vanilla Finest Fixes: fixes and small tweaks for the mods in the Vanilla Finest modpacks, in independent
 * modules, each bound to the mod versions the packs ship.
 * Each module only runs when the mod it works with is installed, in a version the module was made for
 * ({@link ModuleCheck}); otherwise that module does nothing. At start-up every active module is logged
 * ("... module active"), and a module whose mod has another version says why it is off.
 *
 * <ul>
 *   <li><b>Iris</b> (iris/): turns Improved Transparency (Fabulous graphics on 1.21.1) back on when
 *       shaders are disabled, if Iris turned it off for them; on 26.3 also the enchantment glint
 *       crash fix for Iris 1.11.7. Mixins only (see {@link MixinPlugin}).</li>
 *   <li><b>Better Clouds</b> (clouds/): toggle key, automatic Mellow cloud preset, Voxy cloud depth.
 *       Started here when Better Clouds and Fabric API are installed.</li>
 *   <li><b>Elytra Contrails</b> (elytratrails/): skips trail sampling for players and entities whose
 *       trails are switched off, and loops over players only. Mixins only.</li>
 *   <li><b>Touch'n Motion</b>: a seated player's head follows the player's facing instead of the
 *       camera, with every camera mod. Mixin only.</li>
 *   <li><b>Atmospherics</b> (atmospherics/, 26.3): Improved Transparency pipelines for Atmospherics
 *       2.6.8's AirHaze particles, which crash the game without them. Mixin only.</li>
 *   <li><b>ImageIO</b> (imageio/, always on): ImageIO's disk cache is turned off at start-up, so mods
 *       reading textures through ImageIO no longer create a temp file per image on the render thread.</li>
 *   <li><b>Voxy</b> (voxy/, 26.2 and 26.3): lets the game stay on Vulkan with Voxy installed and
 *       switches Voxy off for that session. Mixins only.</li>
 * </ul>
 */
public class VanillaFinestFixes implements ClientModInitializer {
    public static final String MOD_ID = "vanilla-finest-fixes";
    public static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Fixes");

    @Override
    public void onInitializeClient() {
        FabricLoader loader = FabricLoader.getInstance();

        if (ImageIOCache.init()) {
            LOGGER.info("ImageIO module active (ImageIO's disk cache is off, images are buffered in memory)");
        }

        if (check("Iris", "iris")) {
            if (ModuleCheck.inThisVersion("iris.glint")) {
                if (ModuleCheck.matches("iris.glint")) {
                    LOGGER.info("Iris module active (with the enchantment glint fix for {})", ModuleCheck.expectedText("iris.glint"));
                } else {
                    LOGGER.info("Iris module active; its glint fix is off: made for {}, found {}",
                            ModuleCheck.expectedText("iris.glint"), ModuleCheck.installedVersion("iris"));
                }
            } else {
                LOGGER.info("Iris module active");
            }
        }

        if (check("Better Clouds", "betterclouds")) {
            if (loader.isModLoaded("fabric-api")) {
                CloudsToggle.init();
                LOGGER.info("Better Clouds module active");
            } else {
                LOGGER.warn("Better Clouds is installed but Fabric API is not; the Better Clouds module is off");
            }
        }

        if (check("Elytra Contrails", "elytratrails")) {
            LOGGER.info("Elytra Contrails module active");
        }

        if (check("Touch'n Motion", "touch_n_motion")) {
            LOGGER.info("Touch'n Motion module active");
        }

        if (check("Atmospherics", "atmospherics")) {
            LOGGER.info("Atmospherics module active (Improved Transparency pipelines for its AirHaze particles)");
        }

        if (check("Voxy", "voxy")) {
            LOGGER.info("Voxy module active");
        }

    }

    /**
     * True when the module is in this jar and its mod is installed in a version it was made for. When the
     * mod is installed in another version, logs that the module is off. Nothing is logged without the mod.
     */
    private static boolean check(String module, String key) {
        if (!ModuleCheck.inThisVersion(key) || !FabricLoader.getInstance().isModLoaded(ModuleCheck.modId(key))) {
            return false;
        }
        if (ModuleCheck.matches(key)) {
            return true;
        }
        LOGGER.warn("{} module off: made for {}, found {}", module, ModuleCheck.expectedText(key),
                ModuleCheck.installedVersion(ModuleCheck.modId(key)));
        return false;
    }
}
