package io.github.hiveko.vanillafinest.fixes.mixin.weather;

import io.github.hiveko.vanillafinest.fixes.weather.WeatherRadius;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Sodium 0.9.1 and 0.8.12 build their Weather Effect Radius slider with a range of their own (3–10); its top
 * goes to {@link WeatherRadius#MAX} like Minecraft's option ({@link OptionsWeatherRadiusMixin}). Sodium 0.9.3+
 * reads the range from Minecraft's option and needs nothing. Targeted by name; "sodium.weather" module.
 */
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder", remap = false)
public abstract class SodiumWeatherRangeMixin {
    @ModifyArg(method = "buildQualityPage",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=sodium:quality.weather")),
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/config/option/Range;<init>(III)V",
                    ordinal = 0, remap = false),
            index = 1, remap = false)
    private int vanillaFinest$weatherRadiusMax(int max) {
        return Math.max(max, WeatherRadius.MAX);
    }
}
