# STEP 5.8 — Anatomy Implementation

## What changed

This pass replaces incremental visual tuning with a blueprint-locked reconstruction.

### Structural changes

- Thorax increased to nine overlapping rib slices with explicit dorsal/ventral contour masses.
- Abdomen increased to eight taper slices to create a real waist.
- Pelvis increased to eight slices and larger iliac/gluteal masses.
- Neck increased to fifteen visual masses over the three compatible neck bones.
- Head rebuilt around a low elongated cranium, four-stage snout taper, anterolateral eyes, jaw muscles, brow ridges and crown continuity.
- Tail increased to twenty visual masses plus terminal tip.
- Forelimbs and hindlimbs were re-proportioned for a lower crouched reptilian posture.
- Feet retain pads, articulated toes, claws and dewclaws; no rectangular foot block is introduced.

### Wing reconstruction

The previous membrane strategy could still behave like a broad bird slab because most membrane geometry lived in wing-root space. STEP 5.8 removes that architecture.

Membrane webs are now attached to the same articulated hierarchy as their nearest bones:

- root/body web → wing root,
- arm web → upper arm,
- forearm web → forearm,
- four distal radial web sectors → hand.

This means wing articulation moves skeleton and membrane together.

The three principal digits use distinct lengths and large yaw divergence. Distal membrane sectors use distinct radial orientations and quadratic taper, creating separate bat/dragon membrane bays and a scalloped trailing edge.

### Pose bug fixed

The previous shared animation used assignment for `neck02.xRot`, `neck03.xRot`, and `head.xRot` after resetting the neutral pose. That erased the neutral neck/head S-curve every frame.

STEP 5.8 changes these to additive pitch animation. The locked neutral head/neck geometry therefore remains active while still allowing look pitch.

### Neutral pose

- Head: forward with slight downward predatory bias.
- Neck: low S-curve.
- Body: low and nearly level.
- Wings: moderately elevated but strongly rear-swept with an articulated shoulder/forearm break.
- Forelegs: visibly flexed.
- Hindlegs: strong digitigrade Z/S.

## Compatibility preserved

The following compatible bones remain:

- `body`
- `neck_01`, `neck_02`, `neck_03`
- `head`, `jaw`
- `left_wing_root`, `right_wing_root`
- `upper_arm`, `forearm`
- `left_foreleg`, `right_foreleg`, each with `lower_leg`
- `left_hindleg`, `right_hindleg`, each with `lower_leg`
- `tail_01`, `tail_02`, `tail_03`, `tail_04`

No renderer, entity, stage, size-tier, worldgen, AI or long-range-rendering source is included in this patch.
