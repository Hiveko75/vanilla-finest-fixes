# Vanilla Finest Helper

Small client-side fixes and tweaks for the Vanilla Finest modpacks, in one Fabric mod. It replaces
Better Clouds Toggle, Iris Glint Fix and Voxy Disabler on Vulkan.

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

## Better Clouds part (needs Better Clouds and Fabric API)

- **Toggle key** for Better Clouds' *Enable* option (*Controls → Key Binds → Better Clouds →
  Toggle Better Clouds*, unbound by default). The key name is the same as in Better Clouds Toggle,
  so an existing binding is kept.
- **Mellow cloud preset:** with a Mellow shader pack active, a Better Clouds preset made for Mellow
  ("Mellow (auto)") is selected, and the previous one comes back afterwards.
- **Voxy cloud depth:** turns on Better Clouds' built-in Voxy support, so with shaders clouds are
  hidden behind distant Voxy terrain.

## Elytra Contrails part (needs Elytra Contrails; 26.3)

Elytra Contrails samples trails up to 60 times per second. Each time it loops over every rendered
entity, and for every flying player and every entity with a trail config it renders the model once
more to find where the trails start. It does that even when those trails are switched off ("Enable
Trail" off, for example "Enable Other's Trails"): switched-off trails are only hidden when drawn.

- **Player trails:** only players are looped over, not every entity.
- **Switched-off trails:** players whose trails are off are not sampled at all, unless a resource pack
  has trail configs for elytra or player models, or another mod uses the Elytra Contrails API (those
  can switch single trails back on). Then, as for other entities, an entity whose trails all turn out
  to be off is skipped and checked again a second later, so a settings change applies within a second.
- Entities with a trail that is on, like arrows with the built-in Arrow Trails pack, are sampled
  exactly as before.

Made for Elytra Contrails 1.6.10 (26.3).

## Touch'n Motion part (needs Touch'n Motion; 26.3)

Touch'n Motion 1.0.5 turns a seated player's head (riding a horse, in a minecart, leaning on a fence, in
a cockpit) towards the camera's direction. That was made for Shoulder Surfing, where the player looks
where the camera looks. With Leawind's Third Person the camera moves around the player freely, so the
rider looked in the wrong direction. Without Shoulder Surfing the head now follows the player's own
facing again, as in 1.0.4. With Shoulder Surfing installed nothing changes.

## Voxy part (needs Voxy; 26.2 and 26.3)

Voxy only renders with OpenGL and forces Minecraft onto OpenGL even when Vulkan is selected. With
this part the game stays on the selected Vulkan graphics API and Voxy is switched off for that
session. On OpenGL nothing changes. It does not make Voxy work on Vulkan.

## Files

| File | Minecraft | Parts |
|---|---|---|
| `vanilla-finest-helper-*+mc26.3.jar` | 26.3 | Iris (with glint fix), Better Clouds, Voxy, Elytra Contrails, Touch'n Motion |
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
  (`MixinPlugin`: mixins in `mixin.iris` need Iris, `mixin.voxy` Voxy, `mixin.elytratrails` Elytra
  Contrails). Elytra Contrails is only needed at compile time (`compileOnly`, from Modrinth's Maven).
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
