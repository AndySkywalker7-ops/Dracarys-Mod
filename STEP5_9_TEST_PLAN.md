# STEP 5.9 — Minecraft Test Plan

1. Apply this patch on top of the current STEP 5.8.1/hotfix branch.
2. Build through the existing GitHub Actions workflow.
3. Spawn one Adult/Large and one Colossal/Giant dragon.
4. Capture:
   - exact lateral,
   - exact top,
   - frontal,
   - 3/4 front,
   - low-angle view under the wing.
5. Check head:
   - visibly smaller relative to neck/chest,
   - snout no longer crocodilian,
   - eyes remain lateral,
   - crown/horns/scales remain attached.
6. Check forelimbs:
   - slimmer than hindlimbs,
   - grasping fingers visible,
   - hands do not read as rear feet.
7. Check hindlimbs:
   - larger thigh/gluteal mass,
   - visible reptilian Z,
   - toes contact the ground coherently.
8. Check wings:
   - four structural fingers,
   - no visible sky gaps between membrane sectors,
   - membrane reads as one continuous skin,
   - visible shallow folds/creases,
   - elbow and wrist seams are bridged,
   - no feather/bird silhouette.
9. Check surface detail:
   - shoulder/hip scales, belly scutes, cheek spikes and dorsal crest remain attached during animation.
10. Confirm no regression in long-range render or multipart damage routing.

Acceptance gate:
Do not proceed to final texture/hitbox calibration until side/top/front silhouettes are approved.
