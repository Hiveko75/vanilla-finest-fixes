# Vanilla Finest Helper

Small client-side fixes and tweaks for the Vanilla Finest modpacks, in one Fabric mod. It replaces
Better Clouds Toggle, Iris Glint Fix and Voxy Disabler on Vulkan, and fixes Atmospherics' AirHaze
crash with Improved Transparency.

Each part only runs when the mod it works with is installed. Without that mod the part does
nothing, and the game starts normally.

## Iris part (needs Iris)

- **Better transparency comes back after shaders.** Iris switches Minecraft's better transparency
  off whenever a shader pack is enabled, but never switches it back on. This mod remembers that
  Iris switched it off and switches it back on when shaders are disabled. If it was off before
  shaders were enabled, it stays off.
  - 1.21.11 and 26.x: the *Improved Transparency* option.
  - 1.21.1: the *Fabulous!* graphics mode (Iris lowers it to Fancy).
  - The flag is kept in `config/vanilla-finest-helper.properties`, so it also works after a restart
    with shaders on.
- **Enchantment glint crash fix (26.3 only).** Iris 1.11.7 can crash with "Close the existing
  render pass before performing additional commands" while it loads the glint texture
  ([IrisShaders/Iris#3390](https://github.com/IrisShaders/Iris/issues/3390)). The glint textures
  are now loaded at the start of every frame, before any render pass. Older Iris versions don't
  look the texture up there, so they don't need it.

## Particle transparency fix (26.3)

With *Improved Transparency* on, Minecraft 26.3 draws translucent particles with order-independent
transparency, and every particle type needs its own shaders for that. Atmospherics 2.6.8 has none for
its AirHaze particles, so the game crashed with "OIT pipeline set for particle layer not specified"
as soon as AirHaze appeared.

- **Atmospherics AirHaze** now gets matching shaders: Minecraft's own particle shader without the
  alpha cutoff, like Atmospherics' own, and with Minecraft's additive mode (the one used for energy
  swirls and lightning), which matches how AirHaze blends normally. It looks the same with
  Improved Transparency on or off.
- Any other mod's particle type without these shaders is drawn with Minecraft's translucent particle
  ones instead of crashing (logged once per particle type).

Works with and without Async Particles. Particle types that have their own shaders aren't touched.

## Better Clouds part (needs Better Clouds and Fabric API)

- **Toggle key** for Better Clouds' *Enable* option (*Controls → Key Binds → Better Clouds →
  Toggle Better Clouds*, unbound by default). The key name is the same as in Better Clouds Toggle,
  so an existing binding is kept.
- **Mellow cloud preset:** with a Mellow shader pack active, a Better Clouds preset made for Mellow
  ("Mellow (auto)") is selected, and the previous one comes back afterwards.
- **Voxy cloud depth:** turns on Better Clouds' built-in Voxy support, so with shaders clouds are
  hidden behind distant Voxy terrain.

## Voxy part (needs Voxy; 26.2 and 26.3)

Voxy only renders with OpenGL and forces Minecraft onto OpenGL even when Vulkan is selected. With
this part the game stays on the selected Vulkan graphics API and Voxy is switched off for that
session. On OpenGL nothing changes. It does not make Voxy work on Vulkan.

## Files

| File | Minecraft | Parts |
|---|---|---|
| `vanilla-finest-helper-*+mc26.3.jar` | 26.3 | Iris (with glint fix), particle transparency fix, Better Clouds, Voxy |
| `vanilla-finest-helper-*+mc26.2.jar` | 26.2 | Iris, Better Clouds, Voxy |
| `vanilla-finest-helper-*+mc26.1.2.jar` | 26.1.2 | Iris, Better Clouds |
| `vanilla-finest-helper-*+mc1.21.11.jar` | 1.21.11 | Iris, Better Clouds |
| `vanilla-finest-helper-*+mc1.21.1.jar` | 1.21, 1.21.1 | Iris, Better Clouds |

The mod declares that it provides `better-clouds-toggle`, `iris-glint-fix` and
`voxy-disabler-on-vulkan` (as far as that version contains them), so Fabric refuses to load it
together with the old separate mods.

## How it works

- Iris, Better Clouds and Voxy are not compiled against or bundled. Better Clouds and Iris are
  reached by name at run time; the Iris and Voxy mixins are only applied when that mod is installed
  (`MixinPlugin`: mixins in `mixin.iris` need Iris, mixins in `mixin.voxy` need Voxy).
- Transparency: `OptionInstance.set` is watched for Iris' change (only counted while shaders are
  enabled). At the start of the next renderer reload without shaders (`LevelExtractor` on 26.2+,
  `LevelRenderer` before), the option is set back directly, so there's no second reload, and
  `options.txt` is saved.
- Voxy: Voxy wraps Minecraft's GPU device creation and refuses Vulkan. This wraps the same call one
  layer further out (priority 2000), creates the Vulkan device after Voxy's refusal and skips
  `VoxyClient.initVoxyClient()` on Vulkan. Real Vulkan failures are passed on, so Minecraft's normal
  fallback still works.

## Building

GitHub Actions: *Build* builds every jar; *Release* builds them and publishes a release with the
version's `CHANGELOG.md` section as notes. Locally (Java 25 and 21):

```
./gradlew build -Pmc=26.3        # or 26.2, 26.1.2 -> build/libs
./gradlew -p mc1.21.11 build     # -> mc1.21.11/build/libs
./gradlew -p mc1.21.1 build      # -> mc1.21.1/build/libs
```

Client-side only. License: MIT.
