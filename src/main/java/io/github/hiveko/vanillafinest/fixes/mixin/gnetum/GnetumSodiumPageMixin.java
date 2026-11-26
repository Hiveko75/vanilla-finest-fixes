package io.github.hiveko.vanillafinest.fixes.mixin.gnetum;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.hiveko.vanillafinest.fixes.gnetum.GnetumTexts;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * Gnetum 4.6.5's page in Sodium's Video Settings follows the current language ({@link GnetumTexts}): the HUD
 * element names and their tooltips, which Gnetum made into plain text when the game loaded, and the page
 * names "General" / "Elements" and the "3 passes" value, which are English in its code. Targeted by name
 * (the targets name no Minecraft class, so nothing needs remapping on 1.21.x); "gnetum" module.
 */
@Mixin(targets = "me.decce.gnetum.compat.sodium.SodiumEntrypoint", remap = false)
public abstract class GnetumSodiumPageMixin {
    /** Page "General". */
    @ModifyArg(method = "registerConfigLate",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/config/structure/OptionPageBuilder;setName", ordinal = 0),
            index = 0)
    private Component vanillaFinest$generalPage(Component name) {
        return GnetumTexts.text("page.general", name.getString());
    }

    /** Page "Elements". */
    @ModifyArg(method = "registerConfigLate",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/config/structure/OptionPageBuilder;setName", ordinal = 1),
            index = 0)
    private Component vanillaFinest$elementsPage(Component name) {
        return GnetumTexts.text("page.elements", name.getString());
    }

    /** "Number of Passes" value: "3 passes". */
    @Inject(method = "lambda$registerConfigLate$2", at = @At("HEAD"), cancellable = true)
    private static void vanillaFinest$passes(int passes, CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(GnetumTexts.text("passes", passes + " passes", passes));
    }

    /** Each element's name in the list. */
    @ModifyArg(method = "addElements",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/config/structure/EnumOptionBuilder;setName"),
            index = 0)
    private Component vanillaFinest$elementName(Component name, @Local Map.Entry<String, ?> element) {
        return GnetumTexts.name(element.getKey());
    }

    // The element tooltip: name (id), Gnetum's tooltip text if there is one, then the AUTO/ON/OFF explanation.
    // Gnetum passes the name, whether a tooltip exists and its text in as they were when the game loaded.

    @ModifyVariable(method = "lambda$elementTooltip$0", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static String vanillaFinest$tooltipName(String name, @Local(argsOnly = true, ordinal = 1) String id) {
        return GnetumTexts.name(id).getString();
    }

    @ModifyVariable(method = "lambda$elementTooltip$0", at = @At("HEAD"), argsOnly = true)
    private static boolean vanillaFinest$hasTooltip(boolean has, @Local(argsOnly = true, ordinal = 1) String id) {
        return GnetumTexts.tooltip(id) != null;
    }

    @ModifyVariable(method = "lambda$elementTooltip$0", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static String vanillaFinest$tooltipText(String text, @Local(argsOnly = true, ordinal = 1) String id) {
        String tooltip = GnetumTexts.tooltip(id);
        return tooltip != null ? tooltip : text;
    }
}
