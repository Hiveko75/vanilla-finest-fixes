package io.github.hiveko.vanillafinest.fixes.mixin.texts;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Empty on purpose: it only makes these classes pass through {@code MixinPlugin}, where
 * {@link io.github.hiveko.vanillafinest.fixes.texts.TextPatcher} makes their hard-coded texts translatable.
 * Keep the list the same as the classes in vanilla-finest-fixes.texts.json. The plugin applies it only to
 * classes whose mod is installed. Priority 2000: after other mods' mixins (Head Tracker puts its compass
 * letters into LocatorBar with one).
 */
@Mixin(targets = {
        // Screenshot Gallery 2.1
        "net.hicham.screenshotgallery.gui.GalleryScreen",
        "net.hicham.screenshotgallery.gui.GalleryScreen$SortMode",
        "net.hicham.screenshotgallery.gui.ConfigScreenFactory",
        "net.hicham.screenshotgallery.util.ScreenshotMetadata",
        // Better Advancements 0.6 (its Cloth Config screen)
        "betteradvancements.fabric.config.ConfigValues",
        // Head Tracker 0.5 (compass letters on the locator bar)
        "net.minecraft.client.gui.contextualbar.LocatorBar",
        // MiniHUD 0.41 (the pack's date format setting, "Day {DAY_1}")
        "fi.dy.masa.minihud.info.world.InfoLineTimeWorldFormatted",
        // macOS Input Fixes 1.16 (its options on the Mouse Settings screen, shown on every system)
        "com.hamarb123.macos_input_fixes.client.ModOptions",
        // Cloth Config 26.3 ("Value: %d" on sliders, the search field)
        "me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry",
        "me.shedaniel.clothconfig2.gui.entries.LongSliderEntry",
        "me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry",
        // Better Happy Ghast Controls 1.0.6 (its whole settings screen)
        "com.paulnz.ghastcontrols.config.menu.ConfigScreen",
        // Debugify 26.3 (the bug titles on its settings screen)
        "dev.isxander.debugify.client.gui.ConfigGuiHelper",
        // SpectreLib 0.23 (Cherished Worlds' settings: enum values)
        "com.illusivesoulworks.spectrelib.config.client.screen.EditConfigScreen$EnumConfigEntry",
        "com.illusivesoulworks.spectrelib.config.client.screen.EditConfigScreen$ConfigList",
        // Key Overlay 26.3 (its settings screen)
        "me.seyit.config.ConfigScreen",
        // Mouse Tweaks 2.31 (its settings screen)
        "yalter.mousetweaks.ConfigScreen",
        // Network Protocol Disconnect 2.0.0 (its settings screen)
        "net.mobilelize.netprodis.config.ConfigMenu",
        // Toggle Nametags 2.7.9 (settings screen and chat messages)
        "sh.ndy.screens.ConfigScreen",
        "sh.ndy.ToggleNametagsModMenu",
        "sh.ndy.features.listeners.BossbarToggleListener",
        "sh.ndy.features.listeners.NametagsToggleListener",
        "sh.ndy.features.listeners.SelfNametagToggleListener",
        // EMF Compat Core 2.4.0 (its settings screen, also the Not Enough Animations addon's options)
        "strm.emfcompat.core.client.ConfigScreen",
        "strm.emfcompat.core.client.ConfigScreen$OptionEntry",
        "strm.emfcompat.core.client.ConfigScreen$HeaderEntry",
        // Puzzles Lib 26.3 (the Fabric settings screen of Fuzs's mods: Cut Through, Locked In Slots, Name Tag
        // Upgrade, Reach Behind; its setting names are made from the config keys)
        "fuzs.puzzleslib.fabric.impl.client.config.FabricConfigurationScreen",
        // Multi Key Bindings 1.4 (the Visible / Hidden buttons in its key visibility edit mode)
        "us.kenny.core.MultiKeyBindingEntry",
        "us.kenny.core.profile.KeyEntryVisibilityController"
}, priority = 2000, remap = false)
public abstract class HardcodedTextsMixin {
}
