package io.github.hiveko.vanillafinest.fixes.mixin.betterclouds;

import io.github.hiveko.vanillafinest.fixes.clouds.BetterCloudsPresets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Better Clouds 1.14.x (Minecraft 26.2): loading the built-in presets no longer adds another "Default" every
 * start, and the selected preset stays selected (it was lost when Better Clouds re-sorted the list, so
 * "Mellow (auto)" turned into a "Default"). See {@link BetterCloudsPresets}. Targeted by name; "betterclouds"
 * module.
 */
@Mixin(targets = "com.qendolin.betterclouds.config.Config", remap = false)
public abstract class PresetListMixin {
    @Shadow @Final public List<Object> presets;
    @Shadow @Final public List<Object> noisePresets;
    @Shadow public int selectedPreset;
    @Shadow public int selectedNoisePreset;

    @Unique private Object vanillaFinest$shaderBefore;
    @Unique private Object vanillaFinest$noiseBefore;

    @Inject(method = "loadDefaultPresets", at = @At("HEAD"))
    private void vanillaFinest$rememberSelection(CallbackInfo ci) {
        vanillaFinest$shaderBefore = BetterCloudsPresets.selected(presets, selectedPreset);
        vanillaFinest$noiseBefore = BetterCloudsPresets.selected(noisePresets, selectedNoisePreset);
    }

    @Inject(method = "loadDefaultPresets", at = @At("RETURN"))
    private void vanillaFinest$dedupeAndReselect(CallbackInfo ci) {
        selectedPreset = BetterCloudsPresets.dedupeAndFind(presets, vanillaFinest$shaderBefore, selectedPreset, "shader");
        selectedNoisePreset = BetterCloudsPresets.dedupeAndFind(noisePresets, vanillaFinest$noiseBefore, selectedNoisePreset, "noise");
        vanillaFinest$shaderBefore = null;
        vanillaFinest$noiseBefore = null;
    }
}
