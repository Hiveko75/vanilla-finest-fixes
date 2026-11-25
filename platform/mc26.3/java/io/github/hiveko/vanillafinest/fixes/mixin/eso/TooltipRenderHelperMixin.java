package io.github.hiveko.vanillafinest.fixes.mixin.eso;

import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderHelper;
import io.github.hiveko.vanillafinest.fixes.eso.VanillaTooltips;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Enchanted's Sodium Options draws its option tooltips itself, in a box of its own beside the list. This
 * shows the same text as a normal Minecraft tooltip instead ({@link VanillaTooltips}), so it looks like every
 * other tooltip in the game and follows tooltip resource packs and mods. Enchanted's Sodium Options module
 * (26.3, 1.4.1 only; see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}).
 */
@Mixin(TooltipRenderHelper.class)
public abstract class TooltipRenderHelperMixin {
    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private static void vanillaFinest$vanillaTooltip(TooltipRenderHelper.TooltipRenderState state, Font font,
                                                     GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                                                     float partialTick, CallbackInfo ci) {
        VanillaTooltips.show(state, font, graphics, mouseX, mouseY);
        ci.cancel();
    }
}
