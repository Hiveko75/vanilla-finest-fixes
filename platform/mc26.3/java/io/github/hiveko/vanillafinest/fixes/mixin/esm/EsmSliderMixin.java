package io.github.hiveko.vanillafinest.fixes.mixin.esm;

import io.github.hiveko.vanillafinest.fixes.EsmBlockNames;
import io.github.hiveko.vanillafinest.fixes.EsmSoundNames;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extreme Sound Muffler 4.06's list rows show their text as it was given. In the Blocks list that is the block
 * name ESM saved the first time (English on a new instance), so the list stayed English in another language.
 * After a row is made, a block's English name is replaced by its name in the game's language ({@link EsmBlockNames});
 * sound ids ("minecraft:…") get a readable name ({@link EsmSoundNames}) with the id in the tooltip. Only the shown text changes: ESM still keeps and
 * saves its own names, so muffled blocks stay muffled. Extreme Sound Muffler module (26.3, 4.06 only; see
 * {@link io.github.hiveko.vanillafinest.fixes.MixinPlugin}); targeted by name.
 */
@Mixin(targets = "com.leobeliik.extremesoundmuffler.gui.buttons.soundSlider.ESMSlider", remap = false)
public abstract class EsmSliderMixin {
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void vanillaFinest$blockNameInGameLanguage(CallbackInfo ci) {
        AbstractWidget self = (AbstractWidget) (Object) this;
        Component message = self.getMessage();
        String text = message.getString();
        String shown = EsmBlockNames.shown(text);
        // keep the colour ESM gave the row (green when muffled)
        if (!shown.equals(text)) {
            self.setMessage(Component.literal(shown).withStyle(message.getStyle()));
            return;
        }
        // sound rows: a readable name in the game's language, the id in the tooltip
        String name = EsmSoundNames.name(text);
        if (name != null && !name.equals(text)) {
            self.setMessage(Component.literal(name).withStyle(message.getStyle()));
            self.setTooltip(Tooltip.create(Component.literal(text)));
        }
    }
}
