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
a cockpit) towards the camera's direction. With Leawind's Third Person the camera moves around the
player freely, so the rider looked in the wrong direction, and it looked wrong with shoulder cameras
too. The head now follows the player's own facing again, as in 1.0.4, with every camera mod (Shoulder
Surfing included). Made for Touch'n Motion 1.0.5, which has the same code on every Minecraft version.

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

## Home folders module (first start of an instance)

The first time an instance starts, this module points the folder settings of three mods at the
player's shared Minecraft folder, so all instances use the same folders:

| Mod | Setting | Folder |
|---|---|---|
| Screenshot Gallery 2.x | screenshot folder, for saving and for the gallery (`customScreenshotDir`) | `<minecraft>/screenshots` |
| Litematica | custom schematic folder, switched on | `<minecraft>/schematics` |
| Flashback Settings | Flashback's replay folder (`replayFolder`) | `<minecraft>/flashback/replays` |

- `<minecraft>` is the launcher's Minecraft folder, found the way One World Folder finds it:
  `%APPDATA%\.minecraft` on Windows, `~/Library/Application Support/minecraft` on macOS,
  `~/.minecraft` on Linux. If it does not exist, nothing is changed.
- It runs once per instance, before the mods read their settings, and leaves the file
  `config/vanilla-finest-helper.home-folders.done`. After that it never changes these settings
  again. Delete that file to let it run once more.
- A folder the player already chose is kept: a setting that names a folder which exists on this
  computer is not changed. An empty setting, or a folder that is not there, is replaced.
- Only the settings files of installed mods are changed. A missing settings file is started from
  the modpack's default for it (`config/modpack_defaults/config/`).
- With Screenshot Gallery's folder set, the game saves new screenshots there and the gallery shows
  that folder. Screenshot Gallery 1.x (Minecraft 1.21.1) has no such setting.

## Folders module (Flashback, Axiom)

Two more folders the helper sets, with a settings screen:

| Mod | Folder | How | Versions |
|---|---|---|---|
| Flashback (without Flashback Settings) | where replays are saved and listed | Flashback has no setting, so the helper redirects `Flashback.getReplayFolder`; the folder is `folder.flashback.replays` in `config/vanilla-finest-helper.properties` | 0.43.6, 0.43.3, 0.39.10 |
| Axiom | blueprint folder | Axiom's own setting: `customPath` in the `blueprint` part of `config/axiom/axiom.json` (Axiom reads it once at start-up; the folder must exist, the helper creates it) | 6.1.x |

- Empty means the mod's own folder in the instance (`flashback/replays`, `config/axiom/blueprints`); any
  other value is the folder to use (a full path).
- The Home folders module sets both to the shared Minecraft folder once (see above). For Axiom, a
  folder already set in Axiom's settings is kept.
- **Settings screen:** Mod Menu → Vanilla Finest Helper → Configure, when Cloth Config is installed.
  Per folder: "Shared Minecraft folder", "This instance (the mod's own)" or "Another folder" with a path.
  Without Cloth Config there is no Configure button; the files still work. Axiom's own settings screen
  shows and changes the same Axiom setting.
- Flashback reads the setting each time (a change counts at once); Axiom only at start-up.
- When Flashback Settings is installed (26.3, 26.2), its own setting decides the replay folder and the
  helper's Flashback hook is not applied.
- Files are never moved: replays or blueprints already saved stay where they are.
- The Flashback hook is in its own mixin configuration (`vanilla-finest-helper.optional.mixins.json`,
  not required), so if another mod ever loads Flashback's class too early, the hook is skipped with an
  error in the log instead of stopping the game. Axiom cannot be hooked at all: its mixin plugin loads
  Axiom's main class before other mods' mixins are prepared (that crashed 1.1.0 at start-up).

## Voxy module (needs Voxy; 26.2 and 26.3)

Voxy only renders with OpenGL and forces Minecraft onto OpenGL even when Vulkan is selected. With
this module the game stays on the selected Vulkan graphics API and Voxy is switched off for that
session. On OpenGL nothing changes. It does not make Voxy work on Vulkan.

**Shader packs' Voxy shaders:** a shader pack lists the uniforms its Voxy shader needs. Voxy leaves out
any that Iris does not provide on this Minecraft version, and the pack's own default for it is not part
of Voxy's shader, so the shader fails to compile ("undefined variable"), Voxy draws distant terrain
without the shader pack and posts the error in chat. Complementary r5.10 on 26.3 does this with
`inIceCaves` (ice caves only exist from Minecraft 26.4). The helper now declares each such uniform as
`const float NAME = 0.0;` in front of the pack's code (the pack's own default), but only when the shader
uses it and declares it nowhere, so a shader that already compiles is never changed. The log names
the uniforms once ("Voxy shader fix: …").

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
- Voxy shader uniforms: `IrisShaderPatch.getUniformList()` is captured when Voxy builds the uniform
  block; `IrisVoxyRenderPipelineData.opaqueFragPatch()` / `translucentFragPatch()` get the missing
  declarations in front (`MissingUniforms`). Both mixins are in the non-required configuration.

## Building

GitHub Actions: *Build* builds every jar; *Release* builds them and publishes a release with the
version's `CHANGELOG.md` section as notes. Locally (Java 25 and 21):

```
./gradlew build -Pmc=26.3        # or 26.2, 26.1.2 -> build/libs
./gradlew -p mc1.21.11 build     # -> mc1.21.11/build/libs
./gradlew -p mc1.21.1 build      # -> mc1.21.1/build/libs
```

Client-side only. License: All rights reserved.
