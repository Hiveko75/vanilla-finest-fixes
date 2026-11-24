package io.github.hiveko.vanillafinest.fixes.mixin.iris;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor {
    @Accessor("value")
    void vanillaFinest$setValue(Object value);
}
