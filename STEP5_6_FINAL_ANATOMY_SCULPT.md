# STEP 5.6 — BALANCED Final Anatomy Sculpt

## Purpose
Radical geometry rebuild of the BALANCED Dracarys dragon. The target is anatomy-first voxel sculpture: a long curved reptilian neck, lateral eyes, deep muscular thorax, differentiated abdomen/pelvis, bent terrestrial limbs with non-rectangular feet, a tapered living tail, and huge broad wings whose skeleton intersects the membrane instead of floating outside it.

## High-impact changes
- 12 overlapping visual neck masses across the 3 compatibility neck bones.
- 18 overlapping tail masses across tail_01..04 plus a final tip.
- Thorax rebuilt as 7 rib-cage slices plus sternum, upper back, pectorals, serratus, latissimus and trapezius masses.
- Explicit dorsal wing scapular/deltoid/posterior muscle masses.
- Explicit iliac, gluteal and tail-base masses.
- Anterolateral eye sockets/eyes on both sides of the skull.
- Forelegs rebuilt with shoulder, biceps/triceps, elbow, forearm flexor/extensor, wrist, compact pad and articulated toes.
- Hindlegs rebuilt around a strong digitigrade S/Z chain with gluteus, thigh, knee, folded shin, hock and long tarsus.
- Feet no longer use a single long shoe block: heel pad + central pad + 3 two-stage toes + claws + dewclaw.
- Wings use a muscular root, articulated humerus, elbow, radius/ulna, wrist, hand and three three-stage digits.
- Each wing membrane uses 48 overlapping strips with a broad bell-shaped chord and scalloped trailing edge.
- Membrane thickness crosses the skeleton center plane so the bones read as embedded structural ridges.
- Neutral pose changed to a lower reptilian stance, stronger neck S-curve, digitigrade hind-leg Z and broad slightly raised wings.

## Compatibility
The following compatibility names remain present:
`body`, `neck_01`, `neck_02`, `neck_03`, `head`, `jaw`, `left_wing_root`, `right_wing_root`, `upper_arm`, `forearm`, `left_foreleg`, `right_foreleg`, `left_hindleg`, `right_hindleg`, `lower_leg`, `tail_01`, `tail_02`, `tail_03`, `tail_04`.

## Protected systems
This patch contains no renderer, entity, Stage, SizeTier, tracking, LOD or multipart source file. It therefore does not overwrite those systems. Multipart alignment should be recalibrated only after the new silhouette is visually approved.

## Validation
- STEP5_6_STATIC_VALIDATION.log: 53/53 targeted checks PASS.
- STEP5_6_JAVAC_SYNTAX_SUMMARY.log: no syntax-like diagnostics before expected missing Minecraft/Forge dependencies.
- STEP5_6_BUILD_ATTEMPT.log: real Forge build unavailable in this workspace because `gradle-wrapper.jar` is not bundled and Gradle 8.8 is not installed.

## Minecraft visual acceptance test
Spawn at minimum:
- `/dracarys spawn white medium adult`
- `/dracarys spawn blue giant colossal`

Capture lateral, superior, frontal, rear, 3/4 front and 3/4 rear views.

Priority approval questions:
1. Does the lateral silhouette read as a low, long reptilian dragon rather than a rectangle on legs?
2. Are both eyes clearly on the anterolateral sides of the skull?
3. Do the hind legs form a convincing digitigrade S/Z?
4. Do the feet read as pads, toes and claws rather than rectangular shoes?
5. Does the thorax visibly outweigh the abdomen and read as muscular volume?
6. From above, do the wings dominate the silhouette with a broad deep membrane rather than long narrow blades?
7. Do the wing bones visually sit inside/intersect the membrane surface?
