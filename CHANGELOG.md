# Changelog

## 1.0.0 (not released yet)

First version. The fix modules of Vanilla Finest Helper (never published) moved here; the helper keeps
the folder features. Mod id `vanilla-finest-fixes`, one jar per Minecraft version (1.21.1, 1.21.11,
26.1.2, 26.2, 26.3). It provides `better-clouds-toggle`, `iris-glint-fix`, `voxy-disabler-on-vulkan` and
`particle-transparency-fix` (as far as that version contains them), so Fabric refuses to load it
together with those old mods. Each module only runs when its mod is installed in a version it was made
for; the log lists the active modules at start-up ("... module active") and says why a module is off
when its mod has another version.

- **Iris (all versions):** Improved Transparency (Fabulous graphics on 1.21.1) is turned back on when
  shaders are disabled, if Iris turned it off for them. **26.3:** the enchantment glint crash of Iris
  1.11.7 ("Close the existing render pass") is fixed.
- **Better Clouds (all versions):** toggle key (F9 in the packs), the "Mellow (auto)" preset is chosen
  automatically while a Mellow shader pack is on, and clouds are hidden behind Voxy's distant terrain.
- **Elytra Contrails (all versions):** trails are only sampled for players and entities that have them
  switched on, and only players are looped over.
- **Touch'n Motion 1.0.5 (all versions):** a seated player's head follows where the player faces, with
  every third-person camera mod.
- **Atmospherics 2.6.8 (26.3):** AirHaze particles get Improved Transparency pipelines, so they no
  longer crash the game with Improved Transparency on.
- **ImageIO (all versions):** ImageIO's disk cache is off, so mods reading images through ImageIO no
  longer write a temp file per image.
- **Hard-coded texts (26.3):** texts that Screenshot Gallery 2.1, Better Advancements 0.6 (its settings
  screen), Head Tracker 0.5 (the compass letters), Cloth Config ("Value:", "Search...") and MiniHUD 0.41 (the packs' date
  format "Day {DAY_1}") write into their code or settings become translatable through `vanilla-finest-fixes.*` keys, so resource packs can translate them;
  Better Advancements' settings get readable English names instead of ids.
- **Enchanted's Sodium Options 1.4.1 (26.3):** its option tooltips are drawn as normal Minecraft tooltips.
- **Head Browser 2.6.x (26.2, 26.1.2, 1.21.11):** only in creative while in a world: taking heads (Head
  Browser also allowed it in survival singleplayer), the browse key and the pause-screen button.
- **Voxy (26.2, 26.3):** the game stays on Vulkan with Voxy installed and Voxy is switched off for that
  session. Shader packs' distant-terrain shaders compile again when the pack uses a value Iris does not
  provide on this Minecraft version (Complementary r5.10 in the End on 26.3: `inIceCaves`); such values
  are set to 0, the pack's own default, and shaders that already compile are not changed.
