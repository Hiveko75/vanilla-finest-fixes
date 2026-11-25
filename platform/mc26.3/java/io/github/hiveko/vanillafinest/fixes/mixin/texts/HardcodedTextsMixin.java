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
        // Cloth Config 26.3 ("Value: %d" on sliders, the search field)
        "me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry",
        "me.shedaniel.clothconfig2.gui.entries.LongSliderEntry",
        "me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry"
}, priority = 2000, remap = false)
public abstract class HardcodedTextsMixin {
}
