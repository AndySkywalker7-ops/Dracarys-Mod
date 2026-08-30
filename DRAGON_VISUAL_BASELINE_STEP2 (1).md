# Dracarys Mod — Dragon Visual Baseline (Step 2)

Branch: `dragon-visual-overhaul`
Baseline date: 2026-08-29
Status before visual replacement: game launches successfully; branch clean; local backup created.

## Current visual pipeline

1. `ModEntities.DRAGON` registers one entity type: `dracarys_dragon`.
2. `ClientModEvents` registers the model layer and renderer.
3. `DracarysDragonRenderer` uses `DracarysDragonModel` and selects the texture from the dragon variant.
4. `DracarysDragonModel` is a vanilla-style `HierarchicalModel` created entirely in Java.
5. `DracarysDragonEntity` controls variant, stage, size tier, conceptual size, render scale, hitbox scaling and flying state.

## Files that define the current prototype

### Visual files — safe target for the overhaul
- `src/main/java/com/dracarys/dracarysmod/client/model/DracarysDragonModel.java`
- `src/main/java/com/dracarys/dracarysmod/client/renderer/DracarysDragonRenderer.java`
- `src/main/java/com/dracarys/dracarysmod/client/ClientModEvents.java`
- `src/main/resources/assets/dracarysmod/textures/entity/dragon/*.png`

### Gameplay files — do NOT rewrite during the first visual pass
- `src/main/java/com/dracarys/dracarysmod/entity/DracarysDragonEntity.java`
- `src/main/java/com/dracarys/dracarysmod/registry/ModEntities.java`
- dragon genetics / stage / size enums
- spawning, taming, combat, drops, growth, worldgen and recipes

## Current prototype geometry

Texture atlas: `128 x 128`.

- Body: 14 × 12 × 22
- Head: 10 × 8 × 10
- Snout: 6 × 4 × 5
- Left wing: 22 × 2 × 12
- Right wing: 22 × 2 × 12
- Tail: 6 × 6 × 24
- Left leg: 4 × 12 × 4
- Right leg: 4 × 12 × 4

The prototype has no articulated neck chain, jaw, forelegs, feet, horns, wing fingers, segmented tail, membranes or separate anatomy families.

## Current animation logic

The model currently animates only:
- head yaw
- limited head pitch
- sinusoidal wing flap while flying
- subtle idle wing motion
- sinusoidal tail sway

There are no dedicated animation states for walking, running, takeoff, landing, gliding, bite, claw, tail attack, roar, sleep, eating, downed state, wake-up or death.

## Current renderer behavior

- One renderer for all dragons.
- Texture path is selected dynamically from `DragonVariant`:
  `textures/entity/dragon/<variant>.png`
- The whole model is uniformly scaled using `DracarysDragonEntity.renderScale()`.
- Renderer shadow radius is currently `1.2F`.

## Current size / hitbox behavior

- Base registered entity size: `2.4F × 2.0F`.
- Visual scale is derived from conceptual dragon length and clamped.
- Physical dimensions are separately clamped more aggressively than render scale.

This separation is intentional and should be preserved during the visual overhaul so giant dragons can look huge without creating unusable collision/pathfinding behavior.

## Rules for the visual overhaul

1. Do not alter taming, combat, spawning, genetics, drops or growth in the first visual pass.
2. Preserve entity ID `dracarys_dragon`.
3. Preserve all current `DragonVariant` IDs and texture lookup compatibility.
4. Preserve saved-world compatibility: do not rename synced entity data or NBT fields.
5. Keep client-only rendering/model code under the client package.
6. Replace visual anatomy incrementally and compile after each meaningful change.
7. Do not merge `dragon-visual-overhaul` into `main` until the new model loads and is tested in game.
8. Keep the current local backup untouched.

## Next implementation target

Build a new articulated dragon skeleton before texturing it.

Recommended hierarchy:

`root`
- `body`
  - `chest`
  - `belly`
  - `neck_01`
    - `neck_02`
      - `neck_03`
        - `head`
          - `jaw`
          - horns / crest
  - `left_wing_root`
    - upper arm
    - forearm
    - wing fingers / membrane sections
  - `right_wing_root`
    - upper arm
    - forearm
    - wing fingers / membrane sections
  - `left_foreleg`
  - `right_foreleg`
  - `left_hindleg`
  - `right_hindleg`
  - `tail_01`
    - `tail_02`
      - `tail_03`
        - `tail_04`

The first visual milestone is anatomy and proportions only. Advanced animation should be added after the articulated hierarchy renders correctly.
