package io.github.hiveko.vanillafinest.fixes.mixin.weather;

import io.github.hiveko.vanillafinest.fixes.weather.WeatherRadius;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Weather Effect Radius up to {@link WeatherRadius#MAX} (Minecraft: 3–10): the largest value of the option's
 * range, the first range created after its "options.weatherRadius" key. Sodium 0.9.3+ takes its slider range
 * from this option; older Sodium versions have their own range ({@link SodiumWeatherRangeMixin}).
 */
@Mixin(Options.class)
public abstract class OptionsWeatherRadiusMixin {
    @ModifyArg(method = "<init>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=options.weatherRadius")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance$IntRange;<init>(IIZ)V", ordinal = 0),
            index = 1)
    private int vanillaFinest$weatherRadiusMax(int max) {
        return Math.max(max, WeatherRadius.MAX);
    }
}
