# Flashlight Mod

A Fabric 1.21.1 mod adding a toggleable, shader-powered flashlight to Minecraft.

## Features

- Right-click to toggle on/off
- 16 preset beam colors via `/flashlight color set <color>`
- Full LambDynamicLights integration for dynamic fill lighting
- Directional shader beam, volumetric glow and dust particles when paired with the [Photon Flashlight Fork](https://github.com/StewyDev65/photon-flashlight)

## Dependencies

- Fabric Loader >= 0.15.0
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [LambDynamicLights](https://modrinth.com/mod/lambdynamiclights) >= 4.8.7

## Building

```bash
./gradlew build
```

Output jar will be in `build/libs/`.

## License

MIT