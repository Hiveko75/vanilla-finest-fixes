package io.github.hiveko.vanillafinest.mixin.iris;

import io.github.hiveko.vanillafinest.iris.TransparencyRestore;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Notices when Iris switches better transparency off because shaders are enabled. */
@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin {
    @Inject(method = "set", at = @At("HEAD"))
    private void vanillaFinest$beforeSet(Object value, CallbackInfo ci) {
        TransparencyRestore.beforeSet((OptionInstance<?>) (Object) this, value);
    }
}
