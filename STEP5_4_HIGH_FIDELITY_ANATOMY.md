# STEP 5.4 — BALANCED v2 High-Fidelity Anatomy

## Scope

This step is a full anatomy rebuild, not an incremental Step 5.3 refinement.
The model remains a six-limbed western dragon with four terrestrial legs and
two independent dorsal wings. Long-range rendering, tracking, renderScale,
DragonStage, DragonSizeTier, AI, combat, taming and worldgen are unchanged.

## What was rebuilt

### Body mass
- Thorax rebuilt as overlapping rib-cage, sternum and shoulder-arch volumes.
- Abdomen is visibly narrower than the thorax.
- Pelvis regains width and overlaps the abdomen and sacrum/tail transition.
- Limb girdle masses fill the wing, foreleg and hindleg attachment zones.

### Head and neck
- Seven visible neck masses are distributed across compatibility bones
  neck_01, neck_02 and neck_03.
- The neutral compatibility pose creates a real S-curve rather than resetting
  the neck to a straight chain.
- Head rebuilt from back skull, temporal cranium, snout base, mid snout, nose,
  cheeks, brows, independent jaw, horns and crown spines.

### Wings
- Two independent dorsal wing roots remain separate from front-leg shoulders.
- Each wing contains shoulder, humerus, elbow, radius/ulna, wrist, hand,
  metacarpal, three divergent primary digits and a small thumb/claw.
- Longest digit dominates the distal silhouette; the two other digits are
  progressively shorter and more swept.
- The old group of isolated membrane fans was replaced by one contiguous
  wing-root-space membrane surface made from overlapping thin strips.
- The strips taper and form a controlled concave trailing edge, while the
  articulated skeleton renders above the membrane.
- Idle pose sweeps the wings backward; flight animation still opens the same
  compatibility chain toward a broad lifting plane.

### Terrestrial limbs
- Four visual legs are retained.
- Forelegs use shoulder -> upper arm -> elbow -> forearm -> wrist -> metacarpal
  -> foot -> toes/claws.
- Hindlegs use hip -> large thigh -> knee -> shin -> hock -> long distal tarsus
  -> foot -> toes/claws.
- Neutral hind-leg rotations create a pronounced digitigrade Z profile.
- Visible distal bridges remove the old floating-foot gap.

### Tail and dorsal silhouette
- Twelve overlapping tail masses are distributed over tail_01..tail_04.
- Width and height taper continuously toward the tip.
- Compatibility-bone X rotations create a shallow vertical curve, while the
  existing tail animation owns lateral sway.
- Dorsal crest is progressive: largest around neck/shoulders and smaller toward
  the rear.

## Compatibility bones preserved

- body
- neck_01 / neck_02 / neck_03
- head / jaw
- left_wing_root / right_wing_root
- upper_arm / forearm in each wing
- left_foreleg / right_foreleg + lower_leg
- left_hindleg / right_hindleg + lower_leg
- tail_01 / tail_02 / tail_03 / tail_04

## Protected systems

SHA-256 comparison against Step 5.3 confirms no change to:
- DracarysDragonRenderer.java
- CleanLongRangeDragonRenderEvents.java
- DracarysDragonEntity.java
- DragonMultipartLayout.java
- DragonStage.java
- DragonSizeTier.java

## Grounding check

A simple X-axis kinematic approximation of the neutral compatibility pose gives:
- ground plane: model Y 24.00
- front foot lower surface: ~Y 24.74
- hind foot lower surface: ~Y 24.36

These values are intentionally near the model ground plane without translating
the entity itself.

## Validation

- Project validator: 424 checks, 0 errors, 0 warnings, PASS.
- Step 5.4 targeted anatomy validation: 36/36 PASS.
- javac syntax-like diagnostic scan: PASS before expected missing Minecraft/Forge
  dependency errors.
- Real Gradle build could not run because this generated workspace does not
  contain gradle-wrapper.jar and Gradle 8.8 is not installed.

## Minecraft test

1. Build the branch in GitHub Actions.
2. Test at minimum:
   - /dracarys spawn white medium adult
   - /dracarys spawn blue giant colossal
3. Capture lateral, frontal, rear, top, front 3/4 and rear 3/4 views.
4. Judge geometry before textures.
5. Top view is the primary wing acceptance view: it must read as an articulated
   fan with a continuous membrane, not bars with hanging rectangles.
