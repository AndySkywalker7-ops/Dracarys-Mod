# STEP 5.2 — BALANCED v2 Base Model Fidelity

## Scope

This pass rebuilds BALANCED v2 geometry around the approved Dracarys rule:
**four terrestrial legs + two independent wings**.

Only model geometry was changed. Long-range rendering, renderer authority,
tracking, entity gameplay, scale, stage/size logic and multipart architecture
remain untouched.

## Main anatomical changes

### Torso
- Thorax rebuilt from separate upper-ribcage, mid-ribcage and sternum masses.
- Abdomen narrowed into front/rear waist masses.
- Pelvis widens again and transitions into a dedicated tail socket.
- Wing scapulae and front-leg shoulder masses are separate anatomical regions.

### Neck and head
- Existing compatibility chain `neck_01 -> neck_02 -> neck_03` is preserved.
- Five visible neck masses create a more progressive taper and curved silhouette.
- Head now uses back skull, main cranium, snout base, mid snout and nose masses.
- Animated `jaw` remains a direct child of `head`.
- Horns and cranial/dorsal spines remain modular.

### Four terrestrial legs
- Front legs remain independent from wings.
- Front legs use shoulder, upper arm, elbow, forearm, wrist, foot, toes and claws.
- Hind legs use hip, thigh, knee, shin/tarsus, ankle, foot, toes and claws.
- Foot-chain vertical offsets target the standard model ground plane (~Y=24)
  without moving the whole entity.

### Independent wings
- Wing roots are higher and slightly behind front-leg shoulders.
- Compatibility hierarchy remains `wing_root -> upper_arm -> forearm`.
- Visible anatomy adds shoulder mass, humerus sections, elbow, radius/ulna,
  wrist, hand/metacarpal, three dominant wing digits and a small thumb.
- Membrane is split into multiple stepped webs instead of one giant slab.
- Static offsets sweep the wing backward so terrestrial idle reads less like a
  vertical flight pose while preserving the animation contract.

### Tail
- Existing `tail_01 -> tail_02 -> tail_03 -> tail_04` chain is preserved.
- Eight visible tapering masses create a longer, smoother counterbalance.

## Compatibility preserved

SHA-256 verified unchanged:
- `DracarysDragonRenderer.java`
- `DracarysDragonEntity.java`
- `DragonMultipartLayout.java`
- `CleanLongRangeDragonRenderEvents.java`
- `AbstractDracarysDragonModel.java`

## Intentionally not changed

- renderScale / visualLength
- Stage / SizeTier
- long-range rendering
- entity tracking / culling
- multipart hitboxes
- AI / combat / taming / worldgen
- final textures / UV polish
- full animation overhaul

## Validation

- STEP 5.2 specific static validation: 42/42 PASS.
- Project validation: 424 checks, 0 errors, 0 warnings, PASS.
- javac syntax-oriented scan: 0 syntax-like diagnostics before expected
  Minecraft/Forge dependency-resolution failures.
- Full Forge build could not run because this workspace does not contain the
  Gradle wrapper JAR and does not have Gradle 8.8 installed.

## Minecraft acceptance test

Spawn:

```
/dracarys spawn white medium adult
/dracarys spawn blue giant colossal
```

Capture:
1. lateral
2. frontal
3. posterior
4. superior
5. 3/4 frontal
6. 3/4 posterior

Evaluate only anatomy and silhouette in this pass. Multipart calibration should
be performed after the new geometry is visually approved.
