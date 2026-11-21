# Vanilla Finest Helper

Small client-side fixes and tweaks for the Vanilla Finest modpacks, in one Fabric mod. It replaces
Better Clouds Toggle, Iris Glint Fix and Voxy Disabler on Vulkan.

Each module only runs when the mod it works with is installed, **in a version the module was made
for**. Without that mod, or with another version of it, the module does nothing and the game starts
normally. At start-up the log lists every module that is active, for example `Elytra Contrails
module active`, and a module whose mod has another version says why it is off, for example
`Elytra Contrails module off: made for Elytra Contrails 1.6.10, found 1.6.11-26.3`.

## Version check

The versions are listed per Minecraft version in
`platform/mc<version>/resources/vanilla-finest-helper.modules.properties`, compared with the number
part of the mod's version (`1.6.10` for `1.6.10-26.3`):

- Modules that change a mod's code or work around a bug of one version need that exact version:
  Elytra Contrails, Touch'n Motion (1.0.5), Voxy, Atmospherics (2.6.8) and Iris' glint fix
  (1.11.7). When that mod is updated, the module switches itself off until it is checked against the
  new version and added to the list.
- Modules that only read a mod's settings by name accept the same minor version: the Iris
  transparency restore (for example Iris 1.11.x) and Better Clouds (for example 1.15.x).
- ImageIO isn't tied to a mod and is always on.

| Minecraft | Iris | glint fix | Better Clouds | Voxy | Elytra Contrails | Touch'n Motion | Atmospherics |
|---|---|---|---|---|---|---|---|
| 26.3 | 1.11.x | 1.11.7 | 1.15.x | 0.2.20 | 1.6.10 | 1.0.5 | 2.6.8 |
| 26.2 | 1.11.x | – | 1.14.x | 0.2.19 | 1.6.9 | 1.0.5 | – |
| 26.1.2 | 1.11.x | – | 1.13.x | – | 1.5.1 | 1.0.5 | – |
| 1.21.11 | 1.10.x | – | 1.13.x | – | 1.4.9 | 1.0.5 | – |
| 1.21.1 | 1.8.x | – | 1.13.x | – | 1.4.7.5 | 1.0.5 | – |

## Iris module (needs Iris)

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

## Better Clouds module (needs Better Clouds and Fabric API)

- **Toggle key** for Better Clouds' *Enable* option (*Controls → Key Binds → Better Clouds →
  Toggle Better Clouds*, unbound by default). The key name is the same as in Better Clouds Toggle,
  so an existing binding is kept.
- **Mellow cloud preset:** with a Mellow shader pack active, a Better Clouds preset made for Mellow
  ("Mellow (auto)") is selected, and the previous one comes back afterwards.
- **Voxy cloud depth:** turns on Better Clouds' built-in Voxy support, so with shaders clouds are
  hidden behind distant Voxy terrain.

## Elytra Contrails module (needs Elytra Contrails)

Elytra Contrails samples trails up to 60 times per second. Each time it loops over every rendered
entity, and for every flying player and every entity with a trail config it renders the model once
more to find where the trails start. It does that even when those trails are switched off ("Enable
Trail" off, for example "Enable Other's Trails"): switched-off trails are only hidden when drawn.

- **Player trails:** only players are looped over, not every entity.
- **Switched-off trails:** players whose trails are off are not sampled at all, unless a resource pack
  has trail configs for elytra or player models, or another mod uses the Elytra Contrails API (those
  can switch single trails back on). Then, as for other entities, an entity whose trails all turn out
  to be off is skipped and checked again a second later, so a settings change applies within a second.
- **Player model trail spawners** (1.21.11 and 26.x; Elytra Contrails for 1.21.1 doesn't have
  them): with "EMF Model Support" on, every sample of a flying player also
  rendered and animated the whole player model a second time, only to look for EMF trail spawner bones.
  When it finds none for a player, that search is skipped for the next 5 seconds (same empty result),
  then checked again. In a full spark profile this was more than half of Elytra Contrails' cost.
- Entities with a trail that is on, like arrows with the built-in Arrow Trails pack, are sampled
  exactly as before.

Made for the Elytra Contrails versions in the packs: 1.6.10 (26.3), 1.6.9 (26.2), 1.5.1 (26.1.2),
1.4.9 (1.21.11) and 1.4.7.5 (1.21.1, which only has player trails).

## Touch'n Motion module (needs Touch'n Motion)

Touch'n Motion 1.0.5 turns a seated player's head (riding a horse, in a minecart, leaning on a fence, in
a cockpit) towards the camera's direction. That was made for Shoulder Surfing, where the player looks
where the camera looks. With Leawind's Third Person the camera moves around the player freely, so the
rider looked in the wrong direction. Without Shoulder Surfing the head now follows the player's own
facing again, as in 1.0.4. With Shoulder Surfing installed nothing changes (the log says the module
is not needed). Made for Touch'n Motion 1.0.5, which has the same code on every Minecraft version.

## Atmospherics module (needs Atmospherics 2.6.8; 26.3)

With Improved Transparency on, Minecraft 26.3 draws translucent particles with order-independent
transparency, and every particle layer needs its own set of pipelines for that. Atmospherics 2.6.8
creates its AirHaze (ambient mist) particle layer without them, so the game crashes ("OIT pipeline set
for particle layer not specified"). This module gives the mist matching pipelines: Minecraft's own
transparency particle pipeline with Atmospherics' look (no alpha cutoff, no face culling) and additive
blending, like the mist's normal blending. Any other particle layer without them gets Minecraft's
translucent particle pipelines instead of crashing. Reported to the Atmospherics author; only applied
with 2.6.8, so a fixed version is left alone. Replaces the separate Particle Transparency Fix mod.

## ImageIO module (always on)

Java's ImageIO buffers images it reads from a stream in a temporary file on disk by default. Xaero's
World Map reads block textures that way on the render thread whenever it meets a block it hasn't
coloured yet, so exploring created and deleted a temp file per texture, which showed up in spark as
frame hitches (about 10% of the slow frames). Vanilla Finest Helper turns that cache off at start-up
(`ImageIO.setUseCache(false)`), so ImageIO buffers in memory instead. This helps every mod that reads
images through ImageIO, so it isn't tied to one mod; block textures are tiny, so memory use doesn't
change noticeably.

## Voxy module (needs Voxy; 26.2 and 26.3)

Voxy only renders with OpenGL and forces Minecraft onto OpenGL even when Vulkan is selected. With
this module the game stays on the selected Vulkan graphics API and Voxy is switched off for that
session. On OpenGL nothing changes. It does not make Voxy work on Vulkan.

## Files

| File | Minecraft | Modules |
|---|---|---|
| `vanilla-finest-helper-*+mc26.3.jar` | 26.3 | Iris (with glint fix), Better Clouds, Voxy, Elytra Contrails, Touch'n Motion, Atmospherics, ImageIO |
| `vanilla-finest-helper-*+mc26.2.jar` | 26.2 | Iris, Better Clouds, Voxy, Elytra Contrails, Touch'n Motion, ImageIO |
| `vanilla-finest-helper-*+mc26.1.2.jar` | 26.1.2 | Iris, Better Clouds, Elytra Contrails, Touch'n Motion, ImageIO |
| `vanilla-finest-helper-*+mc1.21.11.jar` | 1.21.11 | Iris, Better Clouds, Elytra Contrails, Touch'n Motion, ImageIO |
| `vanilla-finest-helper-*+mc1.21.1.jar` | 1.21, 1.21.1 | Iris, Better Clouds, Elytra Contrails (player trails), Touch'n Motion, ImageIO |

The mod declares that it provides `better-clouds-toggle`, `iris-glint-fix`,
`voxy-disabler-on-vulkan` and `particle-transparency-fix` (as far as that version contains them), so Fabric refuses to load it
together with the old separate mods.

## How it works

- Iris, Better Clouds and Voxy are not compiled against or bundled. Better Clouds and Iris are
  reached by name at run time; the Iris and Voxy mixins are only applied when that mod is installed
  (`MixinPlugin` with `ModuleCheck`: mixins in `mixin.iris` need Iris, `mixin.voxy` Voxy,
  `mixin.elytratrails` Elytra Contrails, `mixin.touchnmotion` Touch'n Motion, `mixin.atmospherics`
  Atmospherics, each in a listed version). Elytra Contrails is only needed at compile time
  (`compileOnly`, from Modrinth's Maven); Touch'n Motion is targeted by class name.
- Elytra Contrails: `TrailSkip` (src/) is shared by every version; the full mixin and the player
  model skip are in `modules/elytratrails-model/` (1.21.11 and 26.x), and 1.21.1 has a smaller
  mixin in `platform/mc1.21.1/`.
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
