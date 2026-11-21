package io.github.hiveko.vanillafinest;

import io.github.hiveko.vanillafinest.clouds.CloudsToggle;
import io.github.hiveko.vanillafinest.imageio.ImageIOCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vanilla Finest Helper: small fixes and tweaks for the Vanilla Finest modpacks, in independent modules.
 * Each module only runs when the mod it works with is installed; without it, that module does nothing.
 * At start-up every module that is active is logged ("... module active").
 *
 * <ul>
 *   <li><b>Iris</b> (iris/): turns Improved Transparency (Fabulous graphics on 1.21.1) back on when
 *       shaders are disabled, if Iris turned it off for them; on 26.3 also the enchantment glint
 *       crash fix. Mixins only, applied when Iris is installed (see {@link MixinPlugin}).</li>
 *   <li><b>Better Clouds</b> (clouds/): toggle key, automatic Mellow cloud preset, Voxy cloud depth.
 *       Started here when Better Clouds and Fabric API are installed.</li>
 *   <li><b>Elytra Contrails</b> (elytratrails/): skips trail sampling for players and entities whose
 *       trails are switched off, and loops over players only. Mixins only, applied when Elytra
 *       Contrails is installed.</li>
 *   <li><b>Touch'n Motion</b>: a seated player's head follows the player's facing instead of the
 *       camera unless Shoulder Surfing is installed. Mixin only, applied with Touch'n Motion.</li>
 *   <li><b>ImageIO</b> (imageio/, always on): ImageIO's disk cache is turned off at start-up, so mods
 *       reading textures through ImageIO no longer create a temp file per image on the render thread.</li>
 *   <li><b>Voxy</b> (voxy/, 26.2 and 26.3): lets the game stay on Vulkan with Voxy installed and
 *       switches Voxy off for that session. Mixins only, applied when Voxy is installed.</li>
 * </ul>
 */
public class VanillaFinest implements ClientModInitializer {
    public static final String MOD_ID = "vanilla-finest-helper";
    public static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Helper");

    @Override
    public void onInitializeClient() {
        FabricLoader loader = FabricLoader.getInstance();

        if (ImageIOCache.init()) {
            LOGGER.info("ImageIO module active (ImageIO's disk cache is off, images are buffered in memory)");
        }
        if (loader.isModLoaded("iris")) {
            LOGGER.info("Iris module active");
        }
        if (loader.isModLoaded("betterclouds")) {
            if (loader.isModLoaded("fabric-api")) {
                CloudsToggle.init();
                LOGGER.info("Better Clouds module active");
            } else {
                LOGGER.warn("Better Clouds is installed but Fabric API is not; the Better Clouds module is off");
            }
        }
        if (loader.isModLoaded("elytratrails")) {
            LOGGER.info("Elytra Contrails module active");
        }
        if (loader.isModLoaded("touch_n_motion")) {
            if (loader.isModLoaded("shouldersurfing")) {
                LOGGER.info("Touch'n Motion module not needed: Shoulder Surfing is installed, so riders keep looking where the camera looks");
            } else {
                LOGGER.info("Touch'n Motion module active");
            }
        }
        if (loader.isModLoaded("voxy") && MixinPlugin.HAS_VOXY_MODULE) {
            LOGGER.info("Voxy module active");
        }
    }
}
