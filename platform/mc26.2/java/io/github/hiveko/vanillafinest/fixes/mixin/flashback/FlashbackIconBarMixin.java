package io.github.hiveko.vanillafinest.fixes.mixin.flashback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Flashback puts its replay button next to the title screen's buttons. This moves it into the row of square
 * icon buttons (language, accessibility, Mod Menu …) as the last icon, the same size, and centres the row
 * again. Done after init and on every tick, as other mods add or move icons late. Flashback module (26.3 and 26.2,
 * Flashback 0.43.*); not applied when Flashback Settings is installed, which does the same with its own
 * setting (see {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}). The button is found by its class
 * name, so Fixes doesn't need Flashback to compile.
 *
 * <p>Taken from Flashback Settings 1.6.0 (TitleScreenButtonBarMixin, without its on/off setting):
 * Copyright (c) 2026 Ayden100, MIT License. Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED
 * "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@Mixin(value = TitleScreen.class, priority = 1500)
public abstract class FlashbackIconBarMixin extends Screen {
    @Unique private static final String FLASHBACK_BUTTON = "com.moulberry.flashback.screen.FlashbackButton";
    @Unique private static final int MIN_ICON_SIZE = 12;
    @Unique private static final int MAX_ICON_SIZE = 32;
    @Unique private static final int MAX_BAR_GAP = 8;

    protected FlashbackIconBarMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"), require = 0)
    private void vanillaFinest$replayButtonAfterInit(CallbackInfo ci) {
        vanillaFinest$moveReplayButtonIntoBar();
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 0)
    private void vanillaFinest$replayButtonAfterTick(CallbackInfo ci) {
        vanillaFinest$moveReplayButtonIntoBar();
    }

    @Unique
    private void vanillaFinest$moveReplayButtonIntoBar() {
        AbstractWidget replayButton = null;
        List<AbstractWidget> icons = new ArrayList<>();
        for (GuiEventListener child : this.children()) {
            if (!(child instanceof AbstractWidget widget) || !widget.visible) continue;
            if (FLASHBACK_BUTTON.equals(widget.getClass().getName())) {
                replayButton = widget;
            } else if (widget.getWidth() == widget.getHeight()
                    && widget.getWidth() >= MIN_ICON_SIZE && widget.getWidth() <= MAX_ICON_SIZE) {
                icons.add(widget);
            }
        }
        if (replayButton == null) return;

        List<AbstractWidget> bar = vanillaFinest$findBar(icons);
        if (bar == null) return;

        int size = bar.get(0).getWidth();
        int rowY = bar.get(0).getY();
        int gap = bar.get(1).getX() - bar.get(0).getRight();
        AbstractWidget last = bar.get(bar.size() - 1);

        boolean alreadyInBar = replayButton.getY() == rowY
                && replayButton.getWidth() == size && replayButton.getHeight() == size
                && replayButton.getX() == last.getRight() + gap;

        int minX = bar.get(0).getX();
        int maxX = alreadyInBar ? replayButton.getRight() : last.getRight();
        int center = (minX + maxX) / 2;

        int count = bar.size() + 1;
        int totalWidth = count * size + (count - 1) * gap;
        int x = center - totalWidth / 2;

        for (AbstractWidget icon : bar) {
            icon.setX(x);
            x += size + gap;
        }
        replayButton.setSize(size, size);
        replayButton.setPosition(x, rowY);
    }

    /** The longest row of same-size square buttons with small gaps between them (at least two). */
    @Unique
    private static List<AbstractWidget> vanillaFinest$findBar(List<AbstractWidget> icons) {
        Map<Long, List<AbstractWidget>> rows = new LinkedHashMap<>();
        for (AbstractWidget icon : icons) {
            long key = ((long) icon.getY() << 32) | (icon.getWidth() & 0xFFFFFFFFL);
            rows.computeIfAbsent(key, k -> new ArrayList<>()).add(icon);
        }
        List<AbstractWidget> best = null;
        for (List<AbstractWidget> row : rows.values()) {
            row.sort(Comparator.comparingInt(AbstractWidget::getX));
            List<AbstractWidget> run = new ArrayList<>();
            for (AbstractWidget icon : row) {
                if (!run.isEmpty()) {
                    int gap = icon.getX() - run.get(run.size() - 1).getRight();
                    if (gap < 0 || gap > MAX_BAR_GAP) {
                        best = vanillaFinest$better(best, run);
                        run = new ArrayList<>();
                    }
                }
                run.add(icon);
            }
            best = vanillaFinest$better(best, run);
        }
        return best;
    }

    @Unique
    private static List<AbstractWidget> vanillaFinest$better(List<AbstractWidget> best, List<AbstractWidget> run) {
        if (run.size() < 2) return best;
        return best == null || run.size() > best.size() ? run : best;
    }
}
