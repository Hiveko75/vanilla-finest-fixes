package io.github.hiveko.vanillafinest.folders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.hiveko.vanillafinest.HelperSettings;
import io.github.hiveko.vanillafinest.ModuleCheck;
import io.github.hiveko.vanillafinest.homefolders.HomeFolders;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Folders module: folders the helper chooses for mods that have no setting for them.
 * <ul>
 *   <li>{@link #FLASHBACK}: where Flashback saves and lists replays. Not used when Flashback Settings
 *       is installed, which has its own setting for this.</li>
 *   <li>{@link #AXIOM}: Axiom's blueprint folder (it is always config/axiom/blueprints otherwise).</li>
 * </ul>
 * Each is one line in config/vanilla-finest-helper.properties. An empty value, or no line, means the
 * mod's own folder in the instance; any other value is used as the folder. The Home folders module
 * sets them to the shared Minecraft folder once; the player can change them in the helper's settings
 * screen (Mod Menu, with Cloth Config) or in the file.
 */
public final class Folders {
    private static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Helper");

    public enum Folder {
        FLASHBACK("folder.flashback.replays", "flashback", "Flashback replays", "flashback/replays"),
        AXIOM("folder.axiom.blueprints", "axiom", "Axiom blueprints", "axiom/blueprints");

        public final String key;
        public final String module;
        public final String title;
        public final String sharedSubfolder;

        Folder(String key, String module, String title, String sharedSubfolder) {
            this.key = key;
            this.module = module;
            this.title = title;
            this.sharedSubfolder = sharedSubfolder;
        }

        /** True when the helper redirects this folder in this game (mod installed, version checked). */
        public boolean active() {
            if (!ModuleCheck.inThisVersion(module) || !ModuleCheck.matches(module)) return false;
            return this != FLASHBACK || !FabricLoader.getInstance().isModLoaded("flashback-settings");
        }

        /** The folder in the shared Minecraft folder, or null when there is no Minecraft folder. */
        public Path shared() {
            Path home = HomeFolders.minecraftFolder();
            if (home == null) return null;
            Path folder = home;
            for (String part : sharedSubfolder.split("/")) folder = folder.resolve(part);
            return folder;
        }
    }

    public static final Folder FLASHBACK = Folder.FLASHBACK;
    public static final Folder AXIOM = Folder.AXIOM;

    private Folders() {}

    /** The setting as written: an absolute path, or "" for the mod's own folder. */
    public static String value(Folder folder) {
        return HelperSettings.get(folder.key, "").trim();
    }

    public static void set(Folder folder, String value) {
        HelperSettings.set(folder.key, value == null ? "" : value.trim());
    }

    /** The folder to use instead of the mod's own, created if needed; null for the mod's own folder. */
    public static Path override(Folder folder) {
        String text = value(folder);
        if (text.isEmpty()) return null;
        try {
            Path path = Paths.get(text);
            if (!path.isAbsolute()) return null;
            Files.createDirectories(path);
            return path;
        } catch (InvalidPathException | IOException | SecurityException e) {
            LOGGER.warn("{}: cannot use the folder \"{}\" ({}); the mod's own folder is used", folder.title, text, e.toString());
            return null;
        }
    }
}
