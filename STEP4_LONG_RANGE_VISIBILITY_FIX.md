# Dracarys Mod — Step 4.0.2 Long Range Visibility Fix

## Problem confirmed in game

Step 4.0.1 fixed premature frustum culling caused by the dragon visual model being much larger than its collision box. In-game testing then exposed a second ceiling: the dragon remained visible only until the configured entity tracking/render distance.

## Root causes

1. `DracarysDragonEntity.shouldRenderAtSqrDistance(...)` had a hard maximum of **320 blocks**.
2. `ModEntities.DRAGON` used `clientTrackingRange(20)`. In Forge/Minecraft entity tracking range is expressed in chunks, so this is approximately **20 × 16 = 320 blocks**.

Both limits therefore converged around the same distance.

## Changes in Step 4.0.2

### Network/entity tracking ceiling

`clientTrackingRange(20)` -> `clientTrackingRange(32)`

Approximate maximum tracking radius:

- Before: 320 blocks
- After: 512 blocks

### Dynamic visual render distance

Before:

```java
Math.max(128.0D, Math.min(320.0D, conceptualLength() * 8.0D))
```

After:

```java
Math.max(192.0D, Math.min(512.0D, conceptualLength() * 16.0D))
```

This means larger dragons remain visible farther away while small dragons still stop rendering earlier.

## Performance choice

The fix deliberately does **not** use `noCulling = true`.

The 512-block network ceiling is a compromise for a large 300–600 mod modpack. Dragons are intended to be rare, so a higher tracking radius is acceptable, but it is still bounded.

If 512 blocks proves insufficient for colossal dragons, a later configuration option can expose the tracking/render policy.

## Files changed

- `src/main/java/com/dracarys/dracarysmod/entity/DracarysDragonEntity.java`
- `src/main/java/com/dracarys/dracarysmod/registry/ModEntities.java`
