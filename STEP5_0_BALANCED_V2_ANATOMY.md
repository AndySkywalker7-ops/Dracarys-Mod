# Dracarys Mod — STEP 5.0
## BALANCED v2 — Realistic Dragon Skeleton & Proportions

## Scope

This step changes **only the visual geometry of Anatomy 01 — BALANCED**.
The stable systems from the Step 4 checkpoint are deliberately left untouched:

- long-range rendering;
- single-render authority/fallback;
- entity tracking;
- multipart hitbox architecture;
- DragonStage and SizeTier;
- renderScale();
- AI, combat, taming, spawning and worldgen;
- existing animation controller in `AbstractDracarysDragonModel`.

## Design goal

BALANCED v2 is a western quadrupedal dragon designed around believable animal
structure rather than a collection of rectangular beams. The model is still
voxel-based, but its silhouette now has explicit anatomical masses and joints.

## New anatomical structure

### Torso

The former uniform body block is replaced by three distinct masses:

1. `thorax` — broad/deep rib cage and shoulder region;
2. `abdomen` — narrower midsection;
3. `pelvis` — broadened rear support mass.

This creates the intended **wide → narrow → wide** body silhouette.
Separate `left_scapula` and `right_scapula` pieces visually connect the wings
and forelimbs to the thorax.

### Head

The head now separates:

- rear cranium;
- main skull;
- projected snout bridge;
- nose/muzzle;
- movable `jaw`;
- cheek masses;
- left/right brow ridges;
- primary horns;
- secondary horns;
- restrained cranial crest.

The compatibility `head` and `jaw` bones are preserved.

### Neck

`neck_01`, `neck_02`, and `neck_03` are preserved exactly as compatibility
bones, but the geometry progressively tapers toward the head. Each segment also
has a smaller ventral/throat mass and moderate dorsal spines.

### Wings

The wing is rebuilt as a bat-inspired articulated structure:

`wing_root -> upper_arm -> forearm -> hand -> four fingers`

The existing animation bones remain:

- `left_wing_root` / `right_wing_root`;
- `upper_arm`;
- `forearm`.

New children add:

- `hand`;
- four elongated fingers;
- three major membrane regions;
- segmented finger membrane areas;
- wing claw.

The span remains intentionally enormous. Visual complexity comes from the bone
hierarchy and segmented membranes rather than one thick rectangular panel.

### Forelegs

Each foreleg now contains:

- shoulder mass;
- upper limb;
- existing animated `lower_leg`;
- angled `wrist`;
- foot;
- three principal toes;
- outer toe;
- individual claws.

### Hindlegs

Each rear leg is heavier and slightly digitigrade:

- hip/thigh mass;
- existing animated `lower_leg`;
- angled `ankle`;
- broad foot;
- three principal toes;
- outer toe;
- claws.

### Tail

Compatibility bones `tail_01` through `tail_04` remain intact. Geometry tapers
progressively through the chain and finishes with `tail_tip`. Only a small
number of dorsal tail spines are used so BALANCED stays elegant rather than
excessively spiked.

## Compatibility bones preserved

The following names consumed by `AbstractDracarysDragonModel` remain present
with the same required hierarchy:

- `body`
- `neck_01`
- `neck_02`
- `neck_03`
- `head`
- `jaw`
- `left_wing_root` / `right_wing_root`
- `upper_arm`
- `forearm`
- `left_foreleg` / `right_foreleg`
- direct child `lower_leg`
- `left_hindleg` / `right_hindleg`
- direct child `lower_leg`
- `tail_01`
- `tail_02`
- `tail_03`
- `tail_04`

Therefore this step does not require changes to the current animation class.

## Model-space landmarks for the NEXT hitbox calibration step

These are approximate BALANCED v2 landmarks in **model units relative to the
`body` pivot**, before `renderScale()` is applied. They are documentation only;
Step 5.0 intentionally does not modify the multipart hitboxes.

| Region | Approx. body-local center | Notes |
|---|---:|---|
| BODY | `(0, 0, 2)` | thorax + abdomen center |
| NECK | `(0, -5, -37)` | middle of tapered three-segment chain |
| HEAD | `(0, -7, -69)` | skull/snout combined visual center |
| LEFT_WING | `(+82, -7, 2)` | useful broad-phase center in idle pose |
| RIGHT_WING | `(-82, -7, 2)` | mirrored |
| TAIL | `(0, 2, +67)` | middle of articulated tail chain |
| LEGS | `(0, +18, +2)` | combined limb interaction region |

Wing tips extend far beyond the listed wing centers. The next hitbox
recalibration should use multiple sampled points/boxes if exact wing coverage is
required instead of inflating one enormous AABB.

## Scale policy

No DragonStage, SizeTier or renderScale values are changed. BALANCED v2 is
built inside the same model coordinate system and continues to be scaled only
by `DracarysDragonRenderer.scale()`.

## Texture policy

The atlas remains 256 × 256 and the normal variant texture path is unchanged.
This is a geometry evaluation build, so UV reuse/stretching is expected and is
not considered final texture work.

## Performance policy

The model adds articulation strategically instead of creating thousands of
parts. There is no per-frame entity search, new renderer, LOD, shader hook,
fog hook, network state or AI work in this step.

## Validation performed

- Dracarys project validator: 424 checks, 0 errors, 0 warnings.
- STEP 5 compatibility/static checks: 65/65 PASS.
- `javac` parse/type-resolution attempt reported no syntax-shaped diagnostics;
  unresolved Minecraft/Forge symbols are expected without dependencies.
- Protected Step 4 Java files were SHA-256 compared against the checkpoint and
  remained byte-for-byte unchanged.

### Forge build status

A real Forge build could not be executed in this environment because the
workspace intentionally contains no `gradle-wrapper.jar` and the system has no
Gradle 8.8 installation. `STEP5_0_BUILD_ATTEMPT.log` records the failed build
attempt. GitHub Actions remains the authoritative compilation check.

Compilation success must not be confused with an in-game visual test.

## In-game visual validation

After GitHub Actions is green, test:

```mcfunction
/dracarys spawn white medium adult
/dracarys spawn blue giant colossal
```

Capture each from:

1. side;
2. front;
3. rear;
4. top;
5. front three-quarter;
6. rear three-quarter.

Evaluate only geometry and silhouette in this build:

- skull / snout / jaw separation;
- neck taper and curve;
- thorax / abdomen / pelvis masses;
- shoulder-to-wing connection;
- long articulated wing silhouette;
- forelimb and hindlimb joint readability;
- feet / toes / claws;
- progressive tail taper.

Do not evaluate final texture quality, multipart hitbox alignment, new combat or
advanced animation yet.
