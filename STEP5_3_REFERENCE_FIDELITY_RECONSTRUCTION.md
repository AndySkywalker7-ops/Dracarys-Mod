# STEP 5.3 — BALANCED v2 Reference Fidelity Reconstruction

## Scope

This patch rebuilds Anatomy 01 BALANCED around the approved reference while
preserving the user's explicit anatomy rule:

- four terrestrial legs;
- two independent wings.

The reference sheet controls silhouette, mass distribution, neck/head/wing/tail
language and ground posture. The user's 4+2 limb rule overrides any conflicting
limb interpretation in the visual reference.

## What was wrong in Step 5.2

1. Large gaps could appear between wrist/ankle and the feet.
2. The neutral animation reset overwrote much of the authored neck/wing pose
   every frame, making the dragon read much straighter than the geometry implied.
3. The wing web still read as large panels because the long axes were not swept
   back enough in neutral pose.
4. Torso masses remained too linear in side view.
5. Tail taper existed but the transition was still visually rigid.
6. Feet and toes were detached from the visible limb chain at some scales.

## Reconstruction

### Torso

The body is now authored lower and as overlapping anatomical masses:

`deep thorax -> narrow waist -> compact pelvis -> tail root`.

Every transition overlaps. There is no intentional air gap between thorax,
abdomen, pelvis and tail root.

### Neck and head

The compatibility chain remains:

`neck_01 -> neck_02 -> neck_03 -> head -> jaw`

but five visible neck masses overlap across those three animated anchors.
The neutral animation pose now produces an S-curve: the neck rises from the
chest and gradually returns the head to a forward, nearly level orientation.

The head uses staged taper:

`back_skull -> main_cranium -> snout_base -> snout_mid -> nose`

with independent jaw, cheeks, brows, horns and crest.

### Wings

Each independent wing now reads as an articulated lifting structure:

`wing_root -> upper_arm -> elbow -> forearm -> wrist -> hand -> 3 main digits`

plus a small wing thumb.

The dominant wing bones are built from multiple overlapping segments.
The membrane is built from six-strip stepped fans per anatomical region instead
of one giant slab. Neutral wing yaw is swept backward and neutral roll is shallow
so the wings rest as broad folded/swept structures instead of vertical boards.

Flight animation opens the yaw toward the lateral plane before applying flap.

### Legs and feet

All four terrestrial legs remain.

Front:
`shoulder -> upper arm -> elbow -> lower_leg -> wrist -> metapodial -> foot`

Rear:
`hip -> thigh -> knee -> lower_leg -> ankle -> metapodial -> foot`

The metapodial bridge explicitly fills the old visual gap between wrist/ankle
and foot.

Neutral-pose kinematic sanity places the front foot center around Y=23.72 px and
the rear foot center around Y=24.05 px against the model ground plane Y=24 px.

### Tail

The compatibility bones remain `tail_01..04`, carrying eight overlapping visible
masses and a terminal tip. Width and height taper continuously, while static
X rotations introduce a shallow organic curve. Existing Y sway animation remains.

## Compatibility changes

`AbstractDracarysDragonModel` is modified only to correct neutral/flight joint
angles. Gameplay logic is untouched.

`DragonMultipartLayout` is recalibrated to the Step 5.3 model-space landmarks so
the existing seven PartEntity zones remain broadly aligned.

The following protected systems remain byte-for-byte unchanged from Step 5.2:

- `DracarysDragonRenderer.java`
- `CleanLongRangeDragonRenderEvents.java`
- `DracarysDragonEntity.java`

## Validation performed

- STEP 5.3 targeted static checks: 52/52 PASS.
- Whole-project static validation: 424 checks, 0 errors, 0 warnings.
- javac syntax-like diagnostic scan: 0 parse/syntax diagnostics before missing
  Minecraft/Forge dependency errors.
- Gradle build attempted. This workspace still does not contain
  `gradle-wrapper.jar` and Gradle 8.8 is not installed, so a real Forge build
  cannot be claimed here.
- Real in-game visual validation has NOT been performed in this environment.

## Required in-game test

Spawn:

`/dracarys spawn white medium adult`

and:

`/dracarys spawn blue giant colossal`

Inspect:

1. lateral;
2. frontal;
3. rear;
4. top;
5. front 3/4;
6. rear 3/4.

Acceptance points:

- no detached feet;
- all four feet meet the ground;
- neck visibly curves;
- head sits forward rather than continuing a straight body line;
- thorax, waist and pelvis read as different masses;
- tail leaves the pelvis continuously and tapers;
- wings are swept/arched rather than vertical rectangular slabs;
- four legs + two independent wings remain visually obvious;
- long-range rendering remains unchanged.
