# Dracarys Mod — Step 4.0.7E
## Sky Visibility Hard Fix

This patch deliberately stops being subtle.

### Problem confirmed in-game

Long-range 3D rendering works, but shader/world fog washes dragons into bright
sky. The user explicitly requires the dragon to remain clearly distinguishable.

### Hard fix

From 80 blocks onward:

1. Dracarys renders the dragon during `AFTER_LEVEL`, the last Forge level-render
   stage.
2. Vanilla shader fog uniforms are temporarily moved to ~1,000,000 blocks.
3. The dragon buffers are flushed while that fog bypass is active.
4. The original fog values are immediately restored.
5. A strong charcoal 3D outline is rendered around the exact dragon geometry.

This applies to:
- the FULL articulated dragon;
- FAR_3D;
- VERY_FAR_3D.

### Visual policy

This is intentionally assertive:
- full opacity;
- strong dominant variant color;
- dark wings for separation;
- almost-black 3D contour;
- no sprites;
- no HUD impostor;
- no increase in model size.

### What remains unchanged

- dragon size;
- renderScale;
- physical hitbox;
- AI;
- combat;
- growth;
- pathfinding;
- server tracking.

### Test

Use an Adult or Colossal against pure open sky.

At >80 blocks the debug HUD should show increasing values for:
- `Sky-visibility fog bypass`
- `3D contrast outline frames`

Success criterion:
the dragon must remain clearly identifiable as a solid colored 3D creature even
when there is no terrain behind it.

This build is specifically meant to be noticeable, not subtle.
