# Changelog

## 1.0.0 (2026-11-21)

First version. Combines Better Clouds Toggle 1.2.1, Iris Glint Fix and Voxy Disabler on Vulkan 1.0.1 into one mod for Minecraft 1.21.1, 1.21.11, 26.1.2, 26.2 and 26.3. Each part only runs when its mod is installed.

- **Iris:** better transparency comes back after shaders. Iris switches Improved Transparency (Fabulous! graphics on 1.21.1) off for shaders and never back on; it is now switched back on when shaders are disabled, if it was on before. New in all versions.
- **Iris, 26.3:** the enchantment glint crash fix from Iris Glint Fix 1.0.0 ([IrisShaders/Iris#3390](https://github.com/IrisShaders/Iris/issues/3390)).
- **Particle transparency, 26.3:** fixes the crash with Improved Transparency on and Atmospherics' AirHaze particles ("OIT pipeline set for particle layer not specified"). AirHaze gets matching additive Improved Transparency shaders and looks the same as without it; other particle types without these shaders use Minecraft's translucent particle ones instead of crashing.
- **Better Clouds:** toggle key, automatic "Mellow (auto)" cloud preset and Voxy cloud depth from Better Clouds Toggle 1.2.1. The key binding keeps its name, so existing bindings carry over.
- **Voxy, 26.2 and 26.3:** keeps Vulkan selected and switches Voxy off for that session, from Voxy Disabler on Vulkan 1.0.1.

Replaces the three separate mods; Fabric won't load it together with them. Client-side only. Iris, Better Clouds, Voxy and Fabric API are all optional.
