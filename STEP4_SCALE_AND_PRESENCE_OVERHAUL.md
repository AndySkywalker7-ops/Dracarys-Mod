# Dracarys Mod — Step 4.0.3
## Scale & Long-Range Presence Overhaul

Branch target: `dragon-visual-overhaul`

### Purpose
Make the dragon growth stages feel substantially more imposing while keeping physical collision/pathfinding growth conservative, and allow major dragons to remain visible at landmark-scale distances.

### Visual stage multipliers
These multipliers apply **only to rendering / visual presence** on top of the existing biological `conceptualLength()`:

- BABY: `1.00x`
- JUVENILE: `1.25x`
- ADOLESCENT: `1.15x`
- ADULT: `1.35x`
- ANCIENT: `1.25x`
- COLOSSAL: `1.60x`

The existing `getDimensions()` calculation remains based on `conceptualLength()` rather than `visualLength()`. This prevents the physical hitbox from scaling as aggressively as the visual model.

### Long-range visibility
Entity tracking ceiling:

- `clientTrackingRange(32)` -> `clientTrackingRange(64)`
- theoretical Forge tracking radius: up to ~1024 blocks (subject to actual loaded/entity-tracked chunks and game/server view-distance constraints).

Render distance targets by growth stage before size-tier adjustment:

- BABY: 256 blocks
- JUVENILE: 384 blocks
- ADOLESCENT: 448 blocks
- ADULT: 640 blocks
- ANCIENT: 768 blocks
- COLOSSAL: 1024 blocks

Size tier adjusts that target:

- SMALL: 0.85x
- MEDIUM: 1.00x
- LARGE: 1.10x
- GIANT: 1.20x

Final render distance is capped at 1024 blocks.

### Culling
The culling-only bounding box now uses `visualLength()` rather than physical/conceptual length, and is slightly wider/taller to better contain long wings and tail geometry.

This does **not** change the combat collision box.

### Riding
The passenger vertical offset receives a limited visual-stage adjustment (capped at 1.45x) so adult/colossal visual scaling does not leave the rider buried deeply inside the model.

### Expected test commands

```text
/dracarys spawn blue medium juvenile
/dracarys spawn red large adult
/dracarys spawn white giant colossal
```

Validate:

1. visual scale versus previous build;
2. entity remains visible at 300 / 500 / 700+ blocks where chunks/entities remain loaded;
3. colossal silhouette can function as a distant landmark;
4. collision and melee behavior remain manageable;
5. mounted player position remains acceptable.
