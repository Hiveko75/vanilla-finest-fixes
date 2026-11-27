package io.github.hiveko.vanillafinest.fixes.mixin.eso;

import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderHelper;
import io.github.hiveko.vanillafinest.fixes.eso.VanillaTooltips;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Enchanted's Sodium Options draws its option tooltips itself, in a box of its own beside the list. This
 * shows the same text as a normal Minecraft tooltip instead ({@link VanillaTooltips}), so it looks like every
 * other tooltip in the game and follows tooltip resource packs and mods. Enchanted's Sodium Options module
 * (1.4.1 on 26.3, 1.3.1 on 26.2, 1.3.2 on 26.1.2, 1.1.8 on 1.21.11; see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}).
 */
@Mixin(TooltipRenderHelper.class)
public abstract class TooltipRenderHelperMixin {
    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private static void vanillaFinest$vanillaTooltip(TooltipRenderHelper.TooltipRenderState state, Font font,
                                                     GuiGraphics graphics, int mouseX, int mouseY,
                                                     float partialTick, CallbackInfo ci) {
        VanillaTooltips.show(state, font, graphics, mouseX, mouseY);
        ci.cancel();
    }
}
