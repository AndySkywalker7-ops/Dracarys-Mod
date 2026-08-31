# STEP 5.9 — Organic Anatomy Rebuild

## Goal
Translate the approved Dracarys dragon anatomy into a higher-fidelity Minecraft voxel model without allowing large cuboids to dominate the silhouette.

## What was wrong in STEP 5.8
- Head mass and snout length read too crocodilian.
- Forelegs and hindlegs were too similar in visual role.
- Hindquarters lacked a strong propulsion/muscle bias.
- Wing membrane still read as separate plates/sectors instead of one folded skin.
- Wing planform needed more dragon/bat structure and more internal finger support.
- Secondary anatomy was sparse: facial scales, cheek spikes, body scutes and integrated armor detail were missing.
- Several large anatomical masses still had obvious box silhouettes.

## STEP 5.9 changes

### Organic voxel mass system
`addEllipsoidMass()` now builds a smaller core plus side, dorsal, ventral and diagonal contour lobes.
`addSideMass()` uses a smaller muscle core plus outer/inner/dorsal/ventral transition volumes.
This keeps the implementation compatible with `ModelPart`/`CubeListBuilder` while reducing the visual dominance of large axis-aligned boxes.

### Head
- Reduced skull and snout dimensions.
- Shorter muzzle and jaw.
- Maintained clearly anterolateral eyes.
- Added a crown progression, cheek spikes and facial scale plates.
- Main and secondary horns now taper through multiple segments.

### Forelimbs
- Slimmer than hindlimbs.
- Dedicated grasping hand replaces the generic terrestrial foot.
- Four curved grasping digits plus opposing thumb/dew-claw structure.

### Hindlimbs
- Hip, gluteus and thigh masses increased.
- Stronger propulsion silhouette.
- Dedicated load-bearing hind foot retained with long toes and claws.

### Wings
- Four articulated membrane fingers instead of three.
- Continuous overlapping membrane strips with shallow alternating pitch create visible folds.
- Large overlap removes the plate/gap read.
- Explicit elbow/wrist membrane bridges hide parent-space seams.
- Raised fold ribs follow upper arm, forearm and hand.
- Membrane remains thin and bones remain structural.

### Surface anatomy
- Shoulder scales.
- Pelvic scales.
- Ventral belly scutes.
- Denser dorsal crest.
- Facial scales and cheek spikes.
All details are direct children of the anatomical parent so they remain anchored and cannot float independently.

### Neutral pose
- Head/neck pose reduced further toward a forward reptilian carriage.
- Wings use a stronger arch and a less airplane-like sweep.
- Forelimbs flex more deeply for grasp/support.
- Hindlimbs retain a stronger digitigrade Z.

## Protected systems
This patch does not contain:
- `DracarysDragonRenderer`
- `CleanLongRangeDragonRenderEvents`
- `DracarysDragonEntity`
- `DragonStage`
- `DragonSizeTier`

It does not intentionally change long-range rendering, tracking, AI, combat, taming, spawn or worldgen.

## Build status
A full Forge build could not be executed because the preserved complete-project workspace does not bundle `gradle-wrapper.jar` and Gradle 8.8 is not installed in the execution environment.

A Java stub compile was performed against compatible minimal API stubs to catch syntax/signature mistakes in the two modified model classes and passed successfully.
