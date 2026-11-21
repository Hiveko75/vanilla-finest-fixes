package io.github.hiveko.vanillafinest.mixin.iris;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor {
    @Accessor("value")
    void vanillaFinest$setValue(Object value);
}
