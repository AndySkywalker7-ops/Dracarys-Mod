# Dracarys Mod — Step 4.0.6A
## Far Dragon LOD Diagnostics

This is a temporary diagnostic build.

The purpose is to stop guessing why the far-dragon representation is not visibly
taking over after vanilla entity tracking ends.

The HUD displays:

- number of cached dragon presences;
- whether the real dragon is still present in ClientLevel;
- whether a LOD snapshot exists;
- whether the LOD is currently eligible/active;
- current distance and maximum LOD distance;
- number of actual LOD renderer attempts;
- distance of the last renderer attempt;
- when vanilla tracking was lost;
- cached snapshot position.

## How to interpret the test

### Case 1 — Cache entries: 0 while the dragon is visible
The snapshot system is not receiving/keeping the dragon. The bug is before rendering.

### Case 2 — Real entity tracked: NO / LOD active: YES / Render attempts keep increasing
The cache and takeover logic work. If no dragon is visible, the failure is in the
world-render/render-dispatch path.

### Case 3 — Real entity tracked: NO / LOD active: NO
The LOD range/dimension/eligibility test is preventing rendering.

### Case 4 — Real entity tracked: YES
Vanilla still owns the visible dragon. No far proxy should be drawn.

## Important

This step does NOT change:
- dragon size;
- hitbox;
- combat;
- AI;
- tracking range;
- gameplay.

The HUD is intentionally temporary and will be removed after diagnosis.
