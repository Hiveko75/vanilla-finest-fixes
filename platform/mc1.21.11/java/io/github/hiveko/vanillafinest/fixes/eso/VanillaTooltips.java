package io.github.hiveko.vanillafinest.fixes.eso;

import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

/**
 * Enchanted's Sodium Options' tooltip as a normal Minecraft tooltip: the option and its value on the first
 * line, the description (wrapped to the width Enchanted's Sodium Options uses, at most the width Enhanced
 * Tooltips allows), then the performance impact
 * line when the option has one. With the mouse it follows the pointer like every tooltip; with the keyboard
 * it stays where Enchanted's Sodium Options would put it, beside the focused option.
 */
public final class VanillaTooltips {
    private VanillaTooltips() {}

    /**
     * The width the description is wrapped to: Enchanted's Sodium Options' own, but at most half the screen minus
     * 32, the width Enhanced Tooltips wraps tooltip lines to. Wider lines would be wrapped a second time there,
     * which leaves single words on their own lines (seen with the longer Hungarian texts).
     */
    private static int bodyWidth(TooltipRenderHelper.TooltipRenderState state) {
        int own = state.width() - state.padding() * 2;
        int half = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 32;
        return Math.max(100, Math.min(own, half));
    }

    public static void show(TooltipRenderHelper.TooltipRenderState state, Font font, GuiGraphics graphics,
                            int mouseX, int mouseY) {
        TooltipContent content = state.content();
        List<FormattedCharSequence> lines = new ArrayList<>();
        Component value = content.getOptionValue();
        if (value != null) lines.add(value.getVisualOrderText());
        lines.addAll(content.getSplitBody(font, bodyWidth(state)));
        Component impact = content.getPerformanceImpact();
        if (impact != null) lines.add(impact.getVisualOrderText());
        if (lines.isEmpty()) return;

        ClientTooltipPositioner positioner = Minecraft.getInstance().getLastInputType().isKeyboard()
                ? (screenWidth, screenHeight, x, y, width, height) -> new Vector2i(
                        Mth.clamp(state.x() + state.padding(), 4, Math.max(4, screenWidth - width - 4)),
                        Mth.clamp(state.y() + state.padding(), 4, Math.max(4, screenHeight - height - 4)))
                : DefaultTooltipPositioner.INSTANCE;
        graphics.setTooltipForNextFrame(font, lines, positioner, mouseX, mouseY, true);
    }
}
