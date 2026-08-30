# STEP 5.1 — BALANCED v2 Anatomy Refinement

## Scope

This step changes only `BalancedDragonModel.java` and refines visible anatomy.
It does not change render distance, entity logic, multipart hitboxes, tracking,
combat, AI, stage sizing, `renderScale()`, `visualLength()`, shaders or textures.

## Main corrections

### Wings

The wings were previously visually dominated by long rectangular structural
boxes and large membrane slabs. STEP 5.1 redistributes the same large overall
span into an anatomical chain:

- shoulder/root;
- upper arm;
- elbow joint;
- forearm;
- wrist joint;
- hand;
- four divergent fingers;
- stepped proximal/middle/distal membranes;
- four inter-digit membrane panels;
- wing claw.

The longest digit is 74 model units, so the silhouette is driven by long bones
and digits instead of one giant rectangular plate. Thin stepped membrane panels
change trailing depth across the span to approximate a tapered wing edge.

### Grounding

The model root/entity is not moved downward.

Grounding is corrected through leg/foot pivots. The front foot pivot is lowered
to 10.0 model units below the wrist and the hind foot to 9.5 below the ankle.
Before rotations, the lowest body-space foot base points now land at
approximately:

- front: Y 24.8;
- hind: Y 24.3.

This intentionally targets the normal Minecraft model ground plane around Y=24.
Toes are flatter and project forward, with claw tips and a rear pad.

### Limbs

Front limbs now visibly separate:

- shoulder mass;
- upper limb;
- elbow mass;
- forearm;
- wrist;
- foot;
- toes/claws.

Hind limbs separate:

- hip/thigh mass;
- knee mass;
- lower leg;
- ankle;
- foot;
- toes/claws.

The rear leg remains more muscular than the front leg.

### Torso

The trunk is now a more explicit mass progression:

`THORAX -> ABDOMEN/WAIST -> PELVIS -> TAIL BASE`

The thorax uses several overlapping volumes plus sternum and pectoral masses.
The abdomen is visibly narrower. The pelvis widens again and contains a tail
base transition to reduce the appearance of a tail glued to a box.

### Head and neck

The three compatibility neck bones remain unchanged by name, but their visible
cross-sections taper progressively from `neck_01` to `neck_03`.

The head separates cranium, rear skull, muzzle base, snout tip, nose bridge,
cheeks, brows and independent jaw. Primary and secondary horns now have a thick
root plus a thinner child tip.

### Tail

`tail_01` through `tail_04` are preserved. Each segment now contains two
progressively narrower volumes, followed by a tapered tail tip and terminal
spike.

## Compatibility

The following files were verified byte-for-byte unchanged from STEP 5.0:

- `DracarysDragonRenderer.java`
- `DracarysDragonEntity.java`
- `DragonMultipartLayout.java`
- `CleanLongRangeDragonRenderEvents.java`
- `AbstractDracarysDragonModel.java`

Therefore STEP 5.1 does not alter the approved long-range renderer or the
multipart gameplay architecture.

## Validation

- STEP 5.1 specific static validation: 64/64 PASS.
- Existing project validator: 424 checks, 0 errors, 0 warnings, PASS.
- `javac` syntax-oriented scan: no parser/syntax error signatures before the
  expected missing Minecraft/Forge dependency errors.
- Forge build could not run in this workspace because `gradle-wrapper.jar` is
  not bundled and Gradle 8.8 is not installed.

A successful GitHub Actions build is still required before calling this a Forge
compilation success. A successful compile is not an in-game visual validation.

## Minecraft test

Spawn:

```mcfunction
/dracarys spawn white medium adult
/dracarys spawn blue giant colossal
```

Capture:

1. frontal;
2. lateral;
3. posterior;
4. superior;
5. 3/4 frontal;
6. 3/4 posterior.

Evaluate only:

- wing silhouette and recognizable articulation;
- feet touching the terrain in grounded idle;
- thorax/waist/pelvis mass distribution;
- articulated-looking limbs;
- neck/head/tail tapering;
- overall silhouette.

Do not use this build to judge final UV/textures or final multipart calibration.
