# Vanilla Finest Fixes

Client-side fixes and small tweaks for the mods in the Vanilla Finest modpacks, in one Fabric mod. Each
fix is bound to the mod versions the packs ship, so this mod changes with the packs. It replaces Better
Clouds Toggle, Iris Glint Fix, Voxy Disabler on Vulkan and Particle Transparency Fix.

The players' side of the packs (folders, and later the setup screen) is a separate mod,
[Vanilla Finest Helper](https://github.com/Hiveko75/vanilla-finest-helper).

Each module only runs when the mod it works with is installed, **in a version the module was made
for**. Without that mod, or with another version of it, the module does nothing and the game starts
normally. At start-up the log lists every module that is active, for example `Elytra Contrails
module active`, and a module whose mod has another version says why it is off, for example
`Elytra Contrails module off: made for Elytra Contrails 1.6.10, found 1.6.11-26.3`.

## Version check

The versions are listed per Minecraft version in
`platform/mc<version>/resources/vanilla-finest-fixes.modules.properties`, compared with the number
part of the mod's version (`1.6.10` for `1.6.10-26.3`):

- Modules that change a mod's code or work around a bug of one version need that exact version:
  Elytra Contrails, Touch'n Motion (1.0.5), Voxy, Atmospherics (2.6.8), Enchanted's Sodium Options
  (1.4.1), Gnetum (4.6.5) and Iris' glint fix (1.11.7). When that mod is updated, the module switches itself off until it is checked against the
  new version and added to the list.
- Modules that only read a mod's settings by name accept the same minor version: the Iris
  transparency restore (for example Iris 1.11.x) and Better Clouds (for example 1.15.x).
- ImageIO isn't tied to a mod and is always on.

| Minecraft | Iris | glint fix | Better Clouds | Voxy | Elytra Contrails | Touch'n Motion | Atmospherics | Ench. Sodium Options | Head Browser | Gnetum |
|---|---|---|---|---|---|---|---|---|---|---|
| 26.3 | 1.11.x | 1.11.7 | 1.15.x | 0.2.20 | 1.6.10 | 1.0.5 | 2.6.8 | 1.4.1 | – (own port) | 4.6.5 |
| 26.2 | 1.11.x | – | 1.14.x | 0.2.19 | 1.6.9 | 1.0.5 | – | – | 2.6.4 | 4.6.5 |
| 26.1.2 | 1.11.x | – | 1.13.x | – | 1.5.1 | 1.0.5 | – | – | 2.6.4 | 4.6.5 |
| 1.21.11 | 1.10.x | – | 1.13.x | – | 1.4.9 | 1.0.5 | – | – | 2.6.1 | 4.6.5 |
| 1.21.1 | 1.8.x | – | 1.13.x | – | 1.4.7.5 | 1.0.5 | – | – | – (own port) | 4.6.5 |

The hard-coded texts module is not version-checked: it only touches texts that match its list exactly.

## Iris module (needs Iris)

- **Better transparency comes back after shaders.** Iris switches Minecraft's better transparency
  off whenever a shader pack is enabled, but never switches it back on. This mod remembers that
  Iris switched it off and switches it back on when shaders are disabled. If it was off before
  shaders were enabled, it stays off.
  - 1.21.11 and 26.x: the *Improved Transparency* option.
  - 1.21.1: the *Fabulous!* graphics mode (Iris lowers it to Fancy).
  - The flag is kept in `config/vanilla-finest-fixes.properties`, so it also works after a restart
    with shaders on.
- **Enchantment glint crash fix (26.3 only).** Iris 1.11.7 can crash with "Close the existing
  render pass before performing additional commands" while it loads the glint texture
  ([IrisShaders/Iris#3390](https://github.com/IrisShaders/Iris/issues/3390)). The glint textures
  are now loaded at the start of every frame, before any render pass. Older Iris versions don't
  look the texture up there, so they don't need it.
- **Resource packs can translate Iris again (26.3, Iris 1.11.7).** Iris copies its own language file
  into Minecraft's texts again each time the next mod's language file is read, so a resource pack's
  Iris texts were always overwritten (in Hungarian, Iris's own "Be-" / "Ki-" stayed on every shader
  on/off button). When the texts are finished, Iris's language files are read once more in the normal
  order, Iris's own first and resource packs on top. Only Iris's own texts are affected.

## Better Clouds module (needs Better Clouds and Fabric API)

- **Toggle key** for Better Clouds' *Enable* option (*Controls → Key Binds → Better Clouds →
  Toggle Better Clouds*, unbound by default). The key name is the same as in Better Clouds Toggle,
  so an existing binding is kept.
- **Mellow cloud preset:** with a Mellow shader pack active, a Better Clouds preset made for Mellow
  ("Mellow (auto)") is selected, and the previous one comes back afterwards.
- **Preset list (26.2, Better Clouds 1.14):** Better Clouds 1.14 added another "Default" preset on every
  start and lost the selected preset when it re-sorted the list (so "Mellow (auto)" turned back into a
  "Default"). Duplicates are removed by key, as Better Clouds 1.15 does, and the selected preset stays
  selected; existing duplicates disappear on the next start.
- **Voxy cloud depth:** turns on Better Clouds' built-in Voxy support, so with shaders clouds are
  hidden behind distant Voxy terrain. On 26.2 (Better Clouds 1.14) that support is already on but
  compared its cloud depth with Voxy's on the wrong scale while a shader pack is on (Iris 1.11.2 makes
  Minecraft and Voxy use the -1..1 depth range, and Better Clouds 1.14 only allows for that with
  Distant Horizons), and it never used Voxy's projection at all (without Distant Horizons it took a
  placeholder matrix from its Distant Horizons support instead), so clouds were drawn over distant
  mountains. Fixes gives it the range and Voxy's projection, as Better Clouds 1.15 does by itself.

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
frame hitches (about 10% of the slow frames). Vanilla Finest Fixes turns that cache off at start-up
(`ImageIO.setUseCache(false)`), so ImageIO buffers in memory instead. This helps every mod that reads
images through ImageIO, so it isn't tied to one mod; block textures are tiny, so memory use doesn't
change noticeably.

## Voxy module (needs Voxy; 26.2 and 26.3)

Voxy only renders with OpenGL and forces Minecraft onto OpenGL even when Vulkan is selected. With
this module the game stays on the selected Vulkan graphics API and Voxy is switched off for that
session. On OpenGL nothing changes. It does not make Voxy work on Vulkan.

**Shader packs' Voxy shaders:** a shader pack lists the uniforms its Voxy shader needs. Voxy leaves out
any that Iris does not provide on this Minecraft version, and the pack's own default for it is not part
of Voxy's shader, so the shader fails to compile ("undefined variable"), Voxy draws distant terrain
without the shader pack and posts the error in chat. Complementary r5.10 on 26.3 does this with
`inIceCaves` (ice caves only exist from Minecraft 26.4; its `voxy.json` only asks for it on 26.4, but
its volumetric light code uses it from 26.2 on). Vanilla Finest Fixes declares such uniforms as
`const float NAME = 0.0;` in front of the pack's code (the pack's own default): every uniform the pack
asked for that Voxy left out, plus a short list of known ones like `inIceCaves`, but only when the shader
uses it and declares it nowhere, so a shader that already compiles is never changed. The log names
the uniforms once ("Voxy shader fix: …").

## Hard-coded texts module (26.3)

Some mods write their texts straight into their code instead of a language file, so no resource pack can
translate them and English players sometimes see raw option ids. This module makes them normal texts:
when one of the listed classes loads and its mod is installed, each listed text the class uses is looked
up under a `vanilla-finest-fixes.<mod>.<name>` key, with the mod's own text as the fallback. The English
is in this mod's `en_us.json` (readable names where the mod showed ids); other languages come from
resource packs (the Hungarian Translations pack has them).

- **Screenshot Gallery 2.1:** the gallery, its menus, messages and settings screen.
- **Better Advancements 0.6:** the settings screen (it showed ids like `defaultUncompletedIconColor`),
  the tooltips and the criteria detail explanation.
- **Head Tracker 0.5:** the compass letters N, E, S, W on the locator bar.
- **Cloth Config:** "Value: %d" on sliders and the "Search..." field.
- **Better Happy Ghast Controls 1.0.6:** its whole settings screen (profiles, speeds, toggles, server
  settings, the delete dialog).
- **Debugify 26.3:** the bug titles on its settings screen (Debugify keeps them in its code; the explanations
  under them were already translatable).
- **SpectreLib 0.23 (inside Cherished Worlds):** the values of an option with fixed choices (NONE,
  FAVORITED, ALL) and the "Allowed Values:" line in its tooltip. Option names and descriptions are normal
  texts (`<key>.name`, `<key>.description`), which the Hungarian pack fills in.
- **macOS Input Fixes 1.16:** its eight options on the Mouse Settings screen (names, tooltips, ON/OFF).
- **Key Overlay (26.3):** its whole settings screen (12 texts).
- **Mouse Tweaks 2.31:** its whole settings screen, with the value names (17 texts).
- **Network Protocol Disconnect 2.0.0:** its settings screen (5 texts; the title is the mod's name).
- **Toggle Nametags 2.7.9:** its settings screen (options with their values, Yes/No, the Essential note,
  the back buttons) and its chat messages. Its key binding names are already translation keys (the English
  labels), which the Hungarian pack fills in.
- **EMF Compat Core 2.4.0:** its settings screen and the options its addons put there (Core, Not Enough
  Animations: names, value descriptions, On/Off), looked up when the screen draws them.
- **MiniHUD 0.41:** the world time line when `dateFormatMinecraft` is the packs' "Day {DAY_1}" (or
  MiniHUD's own default), so each language can write the day its own way ("{DAY_1}. nap"). A format the
  player typed in stays as it is.

The list is `platform/mc26.3/resources/vanilla-finest-fixes.texts.json`; `mixin.texts.HardcodedTextsMixin`
lists the same classes. Texts not in the list are not touched, and when a mod update changes a text, that
text simply stays in the mod's words. A text built from values ("Delete 3 screenshots?") is listed with
`%s` and keeps its values in the translation (`%1$s` lets a translation leave out the English plural "s").

## Enchanted's Sodium Options module (1.4.1 on 26.3, 1.3.1 on 26.2, 1.3.2 on 26.1.2, 1.1.8 on 1.21.11)

Enchanted's Sodium Options draws its option tooltips itself, in its own box beside the option list. This
module shows the same text (the option and its value, the description, the performance impact) as a
normal Minecraft tooltip, so it looks like every other tooltip and follows tooltip resource packs and
mods. With the mouse the tooltip follows the pointer; with the keyboard it stays beside the focused option.
The description is wrapped to Enchanted's Sodium Options' own width, but at most half the screen minus 32,
the width Enhanced Tooltips wraps tooltip lines to; otherwise its lines were wrapped twice.

## Head Browser module (needs Head Browser 2.6.x; 26.2, 26.1.2, 1.21.11)

Head Browser lets a player take any head in a singleplayer world in every game mode (on servers only in
creative), so in survival it is a free source of heads. With this module, in a world that is not in
creative, heads can't be taken, the browse key does nothing and the pause screen has no Head Browser
button (its "Pause button" option reads as off there; the setting is not changed). On the title screen the
browser works as before. The 26.3 and 1.21.1 packs use the packs' own Head Browser ports, which do the
same themselves.

## Immersive Storms module (needs Immersive Storms 1.8.0; 26.3)

Sandstorm grains are much smaller: Immersive Storms spawns them at dust size 10, which Minecraft caps at 4
(the largest dust); the module makes them 2.2 (redstone dust is 1), so a fresh grain is about one block
pixel (1/16 block) across; grains grow to 2 and 3×4 pixels as they age, as in Immersive Storms. Ambient wind
particles are unchanged.

## Extreme Sound Muffler module (needs Extreme Sound Muffler 4.06; 26.3)

The Blocks list shows block names in the game's language. ESM names each block once, when it first fills its
block cache, and saves that cache, so the names stayed in the language of that first start (English on a new
instance). The module maps such an English name back to the block (from the en_us language files) and shows
the current language's name. Only the shown text changes; ESM's saved names and muffled blocks stay as they are.
Searching still matches ESM's saved (English) names.

## Gnetum module (needs Gnetum 4.6.5; all versions)

Gnetum's page in Sodium's Video Settings (*Gnetum*: General, Elements) follows the current language. Gnetum
builds the page once, when the game has loaded, and turns its HUD element names and their tooltips into
plain text at that moment, so a language picked later (the setup screen adds the computer's language on a
first start) or a resource pack added later only reached them after a restart. Now:

- the element names use Gnetum's own keys (`gnetum.config.element.<id>`, ":" written as "."), looked up each
  time they are drawn, with Gnetum's English name made from the id as the fallback;
- their tooltips use `gnetum.config.element.<id>.tooltip` the same way, also when a tooltip only exists in
  the current language;
- the page names "General" and "Elements" and the "3 passes" value of *Number of Passes*, which are
  English in Gnetum's code, are `vanilla-finest-fixes.gnetum.page.general`, `.page.elements` and
  `.passes` ("%s passes"), so resource packs can translate them.

The Hungarian Translations pack has all of them.

## Weather module (26.3, 26.2, 26.1.2, 1.21.11)

Weather Effect Radius (Video Settings, also on Sodium's page) goes up to **15** instead of Minecraft's 10.
Minecraft's weather renderer keeps its column sizes in a 32 × 32 table around the player, which covers a
radius of 15 without any other change; options.txt accepts values up to 15. Sodium 0.9.3+ (26.3) takes its
slider range from Minecraft's option; Sodium 0.9.1 (26.2, 26.1.2) and 0.8.12 (1.21.11) have their own
range, which is raised too (only those versions, `sodium.weather`). 1.21.1 has no such option.

## Files

| File | Minecraft | Modules |
|---|---|---|
| `vanilla-finest-fixes-*+mc26.3.jar` | 26.3 | Iris (with glint fix), Better Clouds, Voxy, Elytra Contrails, Touch'n Motion, Atmospherics, ImageIO, hard-coded texts, Enchanted's Sodium Options, Immersive Storms, Weather, Gnetum |
| `vanilla-finest-fixes-*+mc26.2.jar` | 26.2 | Iris, Better Clouds, Voxy, Elytra Contrails, Touch'n Motion, ImageIO, Enchanted's Sodium Options, Head Browser, Weather, Gnetum |
| `vanilla-finest-fixes-*+mc26.1.2.jar` | 26.1.2 | Iris, Better Clouds, Elytra Contrails, Touch'n Motion, ImageIO, Enchanted's Sodium Options, Head Browser, Weather, Gnetum |
| `vanilla-finest-fixes-*+mc1.21.11.jar` | 1.21.11 | Iris, Better Clouds, Elytra Contrails, Touch'n Motion, ImageIO, Enchanted's Sodium Options, Head Browser, Weather, Gnetum |
| `vanilla-finest-fixes-*+mc1.21.1.jar` | 1.21, 1.21.1 | Iris, Better Clouds, Elytra Contrails (player trails), Touch'n Motion, ImageIO, Gnetum |

The mod declares that it provides `better-clouds-toggle`, `iris-glint-fix`,
`voxy-disabler-on-vulkan` and `particle-transparency-fix` (as far as that version contains them), so Fabric refuses to load it
together with the old separate mods.

## How it works

- Iris, Better Clouds and Voxy are not compiled against or bundled. Better Clouds and Iris are
  reached by name at run time; the Iris and Voxy mixins are only applied when that mod is installed
  (`MixinPlugin` with `ModuleCheck`: mixins in `mixin.iris` need Iris, `mixin.voxy` Voxy,
  `mixin.elytratrails` Elytra Contrails, `mixin.touchnmotion` Touch'n Motion, `mixin.atmospherics`
  Atmospherics, `mixin.eso` Enchanted's Sodium Options, `mixin.headbrowser` Head Browser,
  `mixin.gnetum` Gnetum and `mixin.betterclouds` Better Clouds (26.2 only; these three targeted by
  class name), each in a listed version). Elytra Contrails and
  Enchanted's Sodium Options are only needed at compile time (`compileOnly`, from Modrinth's Maven);
  Touch'n Motion is targeted by class name.
- Hard-coded texts: `HardcodedTextsMixin` is empty and only applied to listed classes whose mod is
  installed; `MixinPlugin.postApply` hands the class to `TextPatcher`, which puts a call to `Texts`
  after each listed text constant (`Texts.tr`) and string concatenation (`Texts.concat`), and, where a
  group says so, before `Component.literal` (`literal`), after reading a static text array (`arrays`) or
  after listed methods that return a text (`calls`, MiniHUD's `ConfigString.getStringValue`).
  Priority 2000, so code other mods' mixins put into a class (Head Tracker in `LocatorBar`) is there.
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
- Gnetum: `GnetumSodiumPageMixin` (optional configuration) changes `SodiumEntrypoint`: the two
  `OptionPageBuilder.setName` calls and the "passes" formatter lambda get translatable texts, the element's
  `EnumOptionBuilder.setName` gets `GnetumTexts.name(id)` (the map entry's key, via MixinExtras `@Local`),
  and the tooltip lambda's name, "has tooltip" flag and tooltip text are replaced at its start. No target
  names a Minecraft class, so the same mixin works on 1.21.x, where Loom remaps in place.
- Voxy shader uniforms: `IrisShaderPatch.getUniformList()` is captured when Voxy builds the uniform
  block; `IrisVoxyRenderPipelineData.opaqueFragPatch()` / `translucentFragPatch()` get the missing
  declarations in front (`MissingUniforms`). Both mixins are in the non-required configuration
  (`vanilla-finest-fixes.optional.mixins.json`), so a problem there only logs an error.

## Building

GitHub Actions: *Build* builds every jar; *Release* builds them and publishes a release with the
version's `CHANGELOG.md` section as notes. Locally (Java 25 and 21):

```
./gradlew build -Pmc=26.3        # or 26.2, 26.1.2 -> build/libs
./gradlew -p mc1.21.11 build     # -> mc1.21.11/build/libs
./gradlew -p mc1.21.1 build      # -> mc1.21.1/build/libs
```

Client-side only. Settings (only the Iris module keeps one) are in `config/vanilla-finest-fixes.properties`.

## Source and licence

Vanilla Finest Fixes is made for the Vanilla Finest modpacks and ships inside them; it is not published
on its own. The source is public so that players, modpack reviewers and the authors of the mods it works
with can see exactly what it changes in their mods.

It is **not open source**: all rights reserved, see [LICENSE](LICENSE). Please don't copy the code into
other projects or include the mod in other modpacks without asking. Bug reports and questions are
welcome as issues, for example when a fix gets in the way of a mod's own update.
