# Validation report

- Target: Minecraft 1.20.1 / Forge 47.4.10 / Java 17.
- Static project validator: **PASS — 424 checks, 0 errors**.
- JSON syntax: all generated JSON resources parsed successfully.
- Resource consistency: item models/textures, dragon textures, armor layers, recipes and worldgen references checked.
- Dependency ranges: Forge `[47.4.10,48)` and Minecraft `[1.20.1,1.20.2)`.
- External meat tags are optional, preventing missing third-party tags from breaking datapack load.
- Namespace: `dracarysmod`.
- No mixins.
- Client renderer isolated under client package and `Dist.CLIENT` MOD-bus subscriber.
- Natural spawning uses Forge biome modifiers and `SpawnPlacementRegisterEvent`.
- Worldgen uses a registered custom Feature + configured/placed feature JSON.
- Recipes are standard shaped recipes for JEI discovery.
- Java source syntax was scanned after generation; a command-tree syntax error and a duplicate `isFlying()` declaration found during review were corrected.
- Runtime Forge compilation: **NOT completed** in this execution environment because Gradle/ForgeGradle dependencies are not installed and external binary downloads are blocked. See `BUILD_ATTEMPT.log`.
- In-game client launch test: **NOT completed**.
- Dedicated-server launch test: **NOT completed**.

This report intentionally does not claim that the mod JAR is runtime-tested.
