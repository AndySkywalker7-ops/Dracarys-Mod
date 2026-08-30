# Dracarys Mod — Step 4.0.4: Long Range Visibility Only

Branch: `dragon-visual-overhaul`

## Goal
Increase dragon visibility distance without increasing dragon size any further.

## Scale lock
This patch intentionally preserves the Step 4.0.3 visual stage multipliers exactly:

- BABY: 1.00x
- JUVENILE: 1.25x
- ADOLESCENT: 1.15x
- ADULT: 1.35x
- ANCIENT: 1.25x
- COLOSSAL: 1.60x

No growth, attribute, hitbox, combat or model scale values were increased.

## Visibility changes

### Entity tracking ceiling
`clientTrackingRange`: 64 -> 96 chunks

Approximate theoretical network ceiling: 1536 blocks, provided the relevant chunks are actually loaded/tracked by Minecraft.

### Render distance targets
- BABY: 384 blocks
- JUVENILE: 640 blocks
- ADOLESCENT: 768 blocks
- ADULT: 1024 blocks
- ANCIENT: 1280 blocks
- COLOSSAL: 1536 blocks

Size tier applies only a mild render-distance factor and the final result is capped at 1536 blocks.

### Frustum culling
The culling-only AABB was expanded to better encompass the oversized visual wings, neck and tail at distance.

This does NOT change:
- physical collision box
- combat hitbox
- pathfinding dimensions
- riding physics
- health/damage
- dragon model scale

## Practical engine limit
These values are ceilings, not a guarantee that the dragon can be seen through unloaded terrain. Minecraft still needs the entity/chunk to exist client-side. Client render distance, server view distance, simulation/tracking behavior and other performance mods can impose lower practical limits.

## Test
Recommended:

`/dracarys spawn blue medium adult`

`/dracarys spawn white giant colossal`

Move away until terrain itself approaches the player's loaded/rendered horizon and verify that the dragon remains visible while its chunk is still available.
