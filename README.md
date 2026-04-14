# Flashlight Mod

A Fabric 1.21.1 mod adding a toggleable, shader-powered flashlight to Minecraft.

## Features

- Right-click to toggle on/off
- 16 preset beam colors via `/flashlight color set <color>`
- Full LambDynamicLights integration for dynamic fill lighting
- Directional shader beam, volumetric glow and dust particles when paired with the [Photon Flashlight Fork](https://github.com/StewyDev65/photon-flashlight)
- **Multiplayer support** — other players' flashlight beams are visible to you (and yours to them) when both have the mod and shader installed

## Dependencies

- Fabric Loader >= 0.15.0
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [LambDynamicLights](https://modrinth.com/mod/lambdynamiclights) >= 4.8.7
- [Iris Shaders](https://modrinth.com/mod/iris) — required for the directional beam and all visual effects
- [Sodium](https://modrinth.com/mod/sodium) — recommended for performance (Iris ships with it by default)
- [Photon Flashlight Fork](https://github.com/StewyDev65/photon-flashlight) — the custom shader pack required for the beam visuals

## Shader Setup

After installing the Photon Flashlight Fork shader, several features are **off by default** and must be enabled manually:

1. In-game, go to **Video Settings → Shader Packs → Shader Options → Lighting**
2. Enable **FLASHLIGHT** to activate the directional beam
3. Enable **FLASHLIGHT_VOLUMETRIC** for volumetric glow and dust particles
4. Enable **FLASHLIGHT_MULTIPLAYER** to see other players' beams (requires OpenGL 4.3, not supported on macOS)

All settings have sensible defaults once enabled. See the [Photon Flashlight Fork README](https://github.com/StewyDev65/photon-flashlight) for the full list of tunable options.

## Multiplayer

The mod must be installed on both the server and all clients for full functionality. With the mod and shader on all clients:

- Other players' flashlight beams are rendered in your world (up to 4 simultaneous beams)
- LambDynamicLights fill lighting from other players' flashlights is visible to everyone
- The `/flashlight color set <color>` command works on dedicated servers

## Building

```bash
./gradlew build
```

Output jar will be in `build/libs/`.

## License

MIT