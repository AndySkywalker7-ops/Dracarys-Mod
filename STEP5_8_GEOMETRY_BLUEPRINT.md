# STEP 5.8 — GEOMETRY BLUEPRINT LOCK

## Authority and intent

This blueprint is the geometry authority for the BALANCED dragon before any texture or final multipart recalibration. The target is the approved voxel concept first, the black dragon reference second, the approved side silhouette third, and the existing Minecraft captures only as error evidence.

The implementation preserves **4 terrestrial legs + 2 independent wings** even where a visual reference suggests a wyvern.

## Coordinate system

- `X`: lateral; positive = model left, negative = model right.
- `Y`: vertical; positive = down in Minecraft model space.
- `Z`: longitudinal; negative = head/front, positive = tail/rear.
- Blueprint master unit: `TORSO_LENGTH = 100`.
- Implementation conversion used for planning: `1 blueprint unit ~= 0.64 model units`.
- Compatible animation bones remain unchanged; additional detail is added as children.

## Locked global proportions

| Region | Blueprint units | Approx. model units | Notes |
|---|---:|---:|---|
| Torso reference length | 100 | 64 | low cervical root to rear pelvis/tail-root transition |
| Head effective length | 44 | 28 | deliberately longer than initial estimate to match approved predatory profile |
| Neck effective length | 92 | 59 | long low S-curve, not vertical |
| Pelvis effective length | 34 | 22 | broad around hip sockets |
| Tail effective length | 168 | 108 | long taper, 20 visual masses + tip |
| Chest maximum width | 50 | 32 | largest body cross-section |
| Chest effective height | 47 | 30 | includes sternum + dorsal/pectoral contour masses |
| Waist width | 27 | 17 | visibly narrower than thorax and pelvis |
| Pelvis maximum width | 39 | 25 | strong hindquarter |
| Full wingspan | 445 | 285 | approximate neutral skeleton envelope before entity scaling |
| Wing root → longest digit tip | 220 | 141 | distal hand/digits dominate |
| Maximum wing chord | 88 | 56 | localized to central/forearm web, never constant |
| Ground clearance | low | — | four feet targeted to the same local ground plane |

## Major body-region blueprint

| Part | L | W | H | Body-local center / anchor | Default intent |
|---|---:|---:|---:|---|---|
| Thorax | 44 | 50 | 47 | `(0, 0, -5)` | deep rib cage, slightly nose-down |
| Sternum | 28 | 31 | 16 | `(0, +8, -5)` | ventral keel / chest depth |
| Abdomen | 42 | 27 | 22 | `(0, +2, +24)` | narrow waist spline |
| Pelvis | 34 | 39 | 32 | `(0, +2, +38)` | broad iliac/gluteal mass |
| Neck base | — | 27 | 23 | `(0, -2.2, -16)` | thick and low |
| Neck middle | — | 18 | 16 | `(0, -1.3, -37)` | reduced section |
| Neck upper | — | 13 | 12 | `(0, 0, -58)` | tapered into skull |
| Skull | 18 | 28 | 18 | `(0, +0.5, -78)` | broad behind eyes |
| Snout | 28 | 12→6 | 10→5 | forward of skull | staged taper |
| Tail root | — | 27 | 20 | `(0, +1.3, +44)` | muscular continuation of pelvis |

## Landmark lock — body-local target coordinates

These are design landmarks, not entity-world coordinates. They are intended as a reproducible geometric check after baking the model.

| Landmark | X | Y | Z |
|---|---:|---:|---:|
| NOSE TIP | 0.0 | +1.8 | -108.0 |
| SKULL CENTER | 0.0 | +0.5 | -78.0 |
| LEFT EYE | +8.75 | -1.6 | -87.4 |
| RIGHT EYE | -8.75 | -1.6 | -87.4 |
| NECK BASE | 0.0 | -2.2 | -16.0 |
| NECK MID | 0.0 | -1.3 | -37.0 |
| NECK UPPER | 0.0 | 0.0 | -58.0 |
| CHEST CENTER | 0.0 | 0.0 | -5.0 |
| LEFT FRONT SHOULDER | +9.3 | +2.6 | -10.5 |
| RIGHT FRONT SHOULDER | -9.3 | +2.6 | -10.5 |
| LEFT WING ROOT | +10.8 | -6.2 | -5.5 |
| RIGHT WING ROOT | -10.8 | -6.2 | -5.5 |
| ABDOMEN CENTER | 0.0 | +1.6 | +26.0 |
| PELVIS CENTER | 0.0 | +1.8 | +38.0 |
| LEFT REAR HIP | +9.6 | +1.8 | +35.0 |
| RIGHT REAR HIP | -9.6 | +1.8 | +35.0 |
| TAIL BASE | 0.0 | +1.3 | +44.0 |
| TAIL MID | 0.0 | ~+3.0 | ~+101.0 |
| TAIL TIP | 0.0 | ~+5.0 | ~+159.0 |
| LEFT FRONT FOOT CENTER | +9.3 | ~+31 | ~-15 |
| RIGHT FRONT FOOT CENTER | -9.3 | ~+31 | ~-15 |
| LEFT REAR FOOT CENTER | +9.6 | ~+32 | ~+30 |
| RIGHT REAR FOOT CENTER | -9.6 | ~+32 | ~+30 |
| LEFT WING ELBOW | +38.5 | -6.6 | ~+0.5 |
| RIGHT WING ELBOW | -38.5 | -6.6 | ~+0.5 |
| LEFT WING WRIST | +69.6 | -6.8 | ~+10 |
| RIGHT WING WRIST | -69.6 | -6.8 | ~+10 |
| LEFT D1 TIP | ~+143 | variable | ~+35 |
| LEFT D2 TIP | ~+132 | variable | ~+55 |
| LEFT D3 TIP | ~+119 | variable | ~+69 |
| RIGHT D1 TIP | ~-143 | variable | ~+35 |
| RIGHT D2 TIP | ~-132 | variable | ~+55 |
| RIGHT D3 TIP | ~-119 | variable | ~+69 |

## Head lock

- Eyes are anterolateral, not frontal: `X ~= ±8.75` from skull center.
- Brow ridges sit above/outboard of the eye line.
- Cranium is widest posterior to the eyes.
- Snout uses four overlapping stages and narrows toward the nose.
- Jaw remains a compatible independent bone.
- Crown spines taper forward and visually continue the cervical crest.

## Neck lock

- `15` visual masses total: five under each compatible neck bone.
- Each mass overlaps the next and tapers progressively.
- The neutral chain is a **low S**, never an elevated giraffe/skyward pose.
- Neutral base rotations:
  - `neck_01.xRot = +0.045`
  - `neck_02.xRot = -0.070`
  - `neck_03.xRot = +0.030`
  - `head.xRot = -0.025`
- Head-pitch animation is additive so it cannot erase the locked S-curve.

## Torso and musculature lock

- Thorax is built from `9` overlapping rib slices.
- Waist is built from `8` narrower abdomen slices.
- Pelvis is built from `8` widening/narrowing slices.
- Independent mass groups: pectorals, lower pectorals, serratus, latissimus, trapezius, terrestrial shoulders, scapular wing girdle, wing deltoids, iliac masses, gluteals, and tail-root muscles.
- A visual muscle is a core + contour lobes, not one box.

## Limb lock

### Forelimb

`shoulder → upper arm → elbow → forearm → wrist → pad → toes`

Neutral animated pose:
- upper/root: `+0.50 rad`
- lower: `-0.86 rad`

### Hindlimb

`hip → heavy thigh → knee → rearward shin → hock → forward tarsus → foot`

Neutral animated pose:
- upper/root: `+0.82 rad`
- lower: `-1.22 rad`

The silhouette must show a strong reptilian Z/S, not two vertical posts.

### Foot

Each foot contains:
- heel pad,
- central pad,
- 3 articulated toes,
- 3 claws,
- dewclaw + claw.

No large rectangular shoe block is permitted.

## Wing blueprint lock

The wing is explicitly **chiropteran/draconic**, never avian.

### Skeleton hierarchy

`wing root → upper_arm/humerus → elbow → forearm/radius-ulna → wrist → hand/metacarpal → D1/D2/D3`

Approximate one-side effective lengths:

| Element | Model length | Share of effective semi-span |
|---|---:|---:|
| Humerus region | ~25 | ~20% |
| Forearm region | ~32 | ~25% |
| Hand + D1 region | ~66 | ~52% |

Digit lengths:
- D1: `27 + 22 + 17 = 66`
- D2: `23 + 18 + 14 = 55`
- D3: `19 + 15 + 11 = 45`

Digit yaw divergence:
- D1 root: about `-0.10 rad`
- D2 root: about `-0.40 rad`
- D3 root: about `-0.72 rad`

### Membrane architecture

The old single giant root-space membrane is prohibited.

Webs are bone-parented:
- `body_web` → wing root,
- `arm_web` → upper arm,
- `forearm_web` → forearm,
- `d1_web`, `d1_d2_web`, `d2_d3_web`, `rear_web` → hand.

Every web crosses the bone center plane (`Y=-0.55..+0.55` approximately), so the skeleton is embedded in the membrane rather than floating above it.

The hand sectors have different yaw, root depth, distal depth, and lengths. This creates explicit bat/dragon bays and visible concavities instead of a smooth bird-wing ellipse.

### Neutral wing pose

- Root: `yRot ~= ±0.325`, `zRot ~= ±0.305`
- Upper arm: `yRot ~= ±0.105`, opposite `zRot ~= 0.145`
- Forearm: `yRot ~= ±0.185`, small counter `zRot ~= 0.085`

Result: elevated, articulated, rear-swept dragon wings — not vertical sails and not airplane/bird slabs.

## Tail lock

- `20` visual taper masses + terminal tip.
- Four compatible animation bones remain.
- Tail has both vertical and small lateral curvature.
- Base is muscular and continuous with pelvis; distal tip is narrow.

## Acceptance geometry tests

1. **Side silhouette:** low predatory head, long S-neck, deep thorax, flexed legs, long tail, wing arch; no `bar + wings + legs` reading.
2. **Top silhouette:** neck narrow, chest broad, waist narrow, pelvis broad, tail tapered, three diverging wing rays and scalloped membrane bays.
3. **Front:** anterolateral eyes, deep chest, legs under body, wing girdle above shoulder girdle.
4. **Pose:** neutral head points forward/slightly down, never skyward.
5. **Legs:** rear Z/S visibly stronger than foreleg bend.
6. **Feet:** pads/toes/claws individually readable.
7. **Wings:** root, humerus, elbow, forearm, wrist, D1/D2/D3 and membrane visually distinguishable; bones embedded in membrane.
8. **Musculature:** pectoral, shoulder, wing root, thigh, hip and tail-root masses readable without texture.

## Deferred work

Final multipart-hitbox calibration is intentionally deferred until this geometry is approved in-game.
