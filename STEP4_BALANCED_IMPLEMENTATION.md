# DRACARYS MOD — Step 4 / BALANCED implementation

Branch target: `dragon-visual-overhaul`

## Scope

This package replaces only the client-side visual baseline of the dragon.

Gameplay intentionally unchanged:

- AI
- combat
- taming
- growth
- genetics
- drops
- spawning
- worldgen
- hitbox calculations

## New visual architecture

### `AbstractDracarysDragonModel.java`
Shared articulated rig/animation contract for future anatomies.

Current animation states implemented as procedural baseline:

- idle breathing
- head/neck tracking
- ground walking
- flight flap
- flight leg tuck
- segmented tail motion
- downed pose

### `BalancedDragonModel.java`
Anatomy 01 — BALANCED.

Rig contains:

- BODY
- CHEST
- BELLY
- NECK_01 / 02 / 03
- HEAD
- JAW
- LEFT_HORN / RIGHT_HORN
- CREST
- LEFT/RIGHT_WING_ROOT
- UPPER_ARM
- FOREARM
- 4 WING FINGERS per side
- LEFT/RIGHT FORELEG + LOWER_LEG + FOOT
- LEFT/RIGHT HINDLEG + LOWER_LEG + FOOT
- TAIL_01 / 02 / 03 / 04 + TIP

The wings are intentionally much longer than the original prototype.

## Renderer

`DracarysDragonRenderer` currently renders every dragon as BALANCED.

Anatomy selection is deliberately postponed until BALANCED is validated in-game.

## Colors / textures

All 15 texture variants were regenerated at 256x256.

The color name is a **dominant color**, not a full-body paint bucket.

Texture regions combine:

- dominant colored scales
- darker natural secondary scales
- differentiated belly plates
- desaturated/darker wing membranes
- neutral horns and claws
- natural transition shading

Variants retained:

- black
- white
- gray
- red
- crimson
- orange
- gold
- green
- dark_green
- blue
- dark_blue
- turquoise
- purple
- silver
- brown

## Files changed

New:

- `src/main/java/com/dracarys/dracarysmod/client/model/AbstractDracarysDragonModel.java`
- `src/main/java/com/dracarys/dracarysmod/client/model/anatomy/BalancedDragonModel.java`

Updated:

- `src/main/java/com/dracarys/dracarysmod/client/renderer/DracarysDragonRenderer.java`
- `src/main/java/com/dracarys/dracarysmod/client/ClientModEvents.java`
- 15 dragon textures under `assets/dracarysmod/textures/entity/dragon/`

The old `DracarysDragonModel.java` remains temporarily as rollback/reference code but is no longer registered by the renderer.

## Validation performed here

`python3 tools/validate_project.py`

Result:

`424 checks, 0 errors, 0 warnings — PASS`

A Forge compilation could not be executed in this workspace because its generated Gradle wrapper does not include `gradle-wrapper.jar` and Gradle is not installed here.

GitHub Actions remains the authoritative compilation test.

## GitHub test

After copying the patch into the repository:

1. Commit on `dragon-visual-overhaul`.
2. Push origin.
3. Wait for `Build Dracarys Mod`.
4. Do not merge into `main` yet.

Suggested commit:

`Step 4: add BALANCED articulated dragon model`

## In-game validation

Start with:

`/dracarys spawn red medium adult`

Then inspect:

1. Side silhouette.
2. Frontal silhouette.
3. Wing length.
4. Feet/ground alignment.
5. Neck/head tracking.
6. Tail motion.
7. Walking.
8. Flying.
9. Downed pose.
10. Rider position.
11. Red texture: confirm red is dominant, not exclusive.
12. Repeat with black, white, green, blue and gold.

Do not proceed to Anatomy 02 until BALANCED is visually accepted or revised.
