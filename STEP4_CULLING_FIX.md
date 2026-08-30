# Dracarys Mod — Step 4 Render/Culling Fix

This patch fixes dragons disappearing while the player is still nearby.

## Cause
The BALANCED visual model is much larger than the physical entity collision box. Vanilla entity rendering uses the entity culling AABB and distance checks, so a long wing/neck/tail can still be visible while Minecraft considers the small physical box outside the camera/render threshold.

## Fix
`DracarysDragonEntity` now overrides:

- `getBoundingBoxForCulling()` — visual/frustum bounds scale with conceptual dragon length.
- `shouldRenderAtSqrDistance()` — render distance scales dynamically from 128 to 320 blocks depending on dragon size.

This **does not** enlarge the combat/collision hitbox and **does not** set `noCulling = true`.

Current `clientTrackingRange(20)` is intentionally left unchanged; it is already generous and provides a useful upper bound for multiplayer performance.
