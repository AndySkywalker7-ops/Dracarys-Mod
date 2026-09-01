# STEP 5.10 — HEAD / SKULL GEOMETRY BLUEPRINT

## Authority
The approved `STEP5_10_APPROVED_HEAD_REFERENCE.png` turnaround is the geometry authority for this pass. STEP 5.9 is only the implementation baseline. The target is the same approved head translated into Minecraft geometry, not a new interpretation.

## Scope lock
Only the `head` hierarchy and the existing `jaw` hierarchy are rebuilt. `neck_01..03`, body, legs, wings, tail, renderer, tracking, entity logic, stages and gameplay remain unchanged.

## Coordinate system
- X: lateral; positive = model left, negative = model right.
- Y: vertical; positive = down.
- Z: longitudinal; negative = forward toward nose, positive = rear toward neck.
- Head local origin: `head` pivot under `neck_03`.
- Normalized authority: `HEAD_LENGTH = 100`.
- Planning conversion: `1 normalized head unit ≈ 0.273 model units`.
- Intended effective model envelope: approximately 27.3 units from occipital rear to nose tip.

## Locked proportions

| Region | Normalized | Approx. model units | Intent |
|---|---:|---:|---|
| Total head length | 100 | 27.3 | Compact relative to body |
| Maximum posterior skull width | 44 | 12.0 | Broad temporal/occipital region |
| Maximum skull height | 34 | 9.3 | Low predatory profile |
| Snout effective length | 43 | 11.7 | Shorter than STEP 5.9 crocodilian profile |
| Snout root width | 31 | 8.5 | Tapers strongly toward nose |
| Snout tip width | 17 | 4.6 | Narrow wedge tip |
| Snout root height | 19 | 5.2 | Vertically compressed |
| Snout tip height | 10 | 2.7 | Low nose |
| Cranium effective length | 38 | 10.4 | Rear skull + orbital braincase |
| Cranium max width | 44 | 12.0 | Widest behind eyes |
| Cranium effective height | 31 | 8.5 | Crown not included |
| Lower jaw effective length | 60 | 16.4 | Hinge behind orbit, tapered forward |
| Jaw root height | 16 | 4.4 | Powerful posterior adductor region |
| Jaw tip height | 7 | 1.9 | Fine front bite |
| Main horn projected length | 66 | 18.0 | Four tapered segments |
| Secondary horn projected length | 48 | 13.1 | Subordinate to primary pair |

## Primary model-space landmarks

All values are local to `head`, before head animation.

| Landmark | X | Y | Z | Notes |
|---|---:|---:|---:|---|
| Neck connection / head pivot | 0.00 | 0.00 | 0.00 | Existing rig pivot |
| Occipital center | 0.00 | -0.35 | 2.10 | Rear skull volume |
| Main cranium center | 0.00 | -0.38 | -4.40 | Broad braincase |
| Front cranium center | 0.00 | -0.20 | -7.40 | Orbital transition |
| Snout root | 0.00 | 0.25 | -10.30 | Wedge begins |
| Snout middle | 0.00 | 0.45 | -13.90 | Narrower and lower |
| Muzzle center | 0.00 | 0.58 | -17.20 | Fine front muzzle |
| Nose center | 0.00 | 0.62 | -20.00 | Nose mass |
| Nose tip target | 0.00 | 0.62 | -21.85 | Approx. geometry envelope |
| Left eye | +5.25 | -1.75 | -7.85 | Anterolateral |
| Right eye | -5.25 | -1.75 | -7.85 | Anterolateral |
| Left orbital mass | +4.55 | -1.35 | -7.20 | Eye recessed inside |
| Right orbital mass | -4.55 | -1.35 | -7.20 | Eye recessed inside |
| Left cheek | +4.65 | +1.25 | -6.60 | Layered posterior face |
| Right cheek | -4.65 | +1.25 | -6.60 | Layered posterior face |
| Left temporal mass | +4.55 | -0.35 | -2.40 | Widens posterior skull |
| Right temporal mass | -4.55 | -0.35 | -2.40 | Widens posterior skull |
| Lower-jaw hinge pivot | 0.00 | +2.92 | -4.15 | Posterior articulation |
| Left main horn root | +4.15 | -3.55 | +0.80 | Physically enters rear skull |
| Right main horn root | -4.15 | -3.55 | +0.80 | Mirrored |
| Left secondary horn root | +4.75 | -2.85 | -3.60 | Behind/superior to orbit |
| Right secondary horn root | -4.75 | -2.85 | -3.60 | Mirrored |
| Left temporal horn root | +5.15 | -1.05 | +0.10 | Lateral rear skull |
| Right temporal horn root | -5.15 | -1.05 | +0.10 | Mirrored |
| Left nostril ridge | +1.25 | -0.72 | -20.15 | Geometry ridge, not floating black cube |
| Right nostril ridge | -1.25 | -0.72 | -20.15 | Mirrored |

## Side-profile construction lock
The superior profile must flow:
`nose -> muzzle -> snout root -> orbital rise -> cranial crown -> swept horns -> neck`.

The inferior profile must flow:
`nose -> upper jaw -> mouth line -> lower jaw taper -> posterior jaw adductor -> throat/neck`.

No single long cuboid is permitted to define the skull silhouette.

## Top-view construction lock
The approved wedge is:
1. narrow nose,
2. expanding muzzle,
3. broad orbital/temporal skull,
4. slight narrowing into the neck.

The eyes remain lateral and partially forward-facing. The posterior skull, not the muzzle, is the widest head region.

## Skull mass hierarchy
Primary anatomical masses:
- `occipital_mass`
- `rear_skull`
- `main_cranium`
- `front_cranium`
- `snout_root`
- `snout_middle`
- `muzzle`
- `nose_mass`

Bilateral structural masses:
- temporal
- jaw adductor
- cheek
- orbital
- brow outer/inner
- recessed eye

## Jaw lock
- `upper_jaw` is rigid with `head`.
- Existing rig-compatible `jaw` remains the lower-jaw animation bone.
- `jaw` pivots at `(0, 2.92, -4.15)`, behind the majority of the tooth row.
- Rear jaw is deeper/wider than front jaw.
- Tongue and oral floor are children of the moving lower jaw.
- Teeth are individual pieces/groups, never a continuous white bar.

## Horn lock
Primary and secondary horns are hierarchical chains:
`root -> segment_02 -> segment_03 -> tip`.

Every successive segment:
- reduces thickness,
- changes direction,
- remains parented to the previous segment,
- sweeps rearward.

A separate paired temporal horn reinforces the approved rear-skull silhouette.

## Crown / plate lock
The approved crown is produced by:
- 8 central crown spines,
- 6 lateral crown spines per side,
- 4 cheek spikes per side,
- 3 submandibular spikes per side,
- layered forehead plates,
- orbital plates,
- cheek scales,
- jaw plates.

Every element is parented to the head hierarchy; no world-space/floating decorations are introduced.

## Acceptance geometry
STEP 5.10 is approved only if the same identity is visible in:
- front,
- left side,
- right side,
- top,
- bottom,
- rear,
- 3/4 front left,
- 3/4 front right.

Highest priority checks:
1. compact head/body ratio,
2. narrow wedge muzzle,
3. anterolateral eyes,
4. broad posterior skull,
5. articulated tapered jaw,
6. integrated swept horns,
7. layered crown/cheek silhouette,
8. clean connection to existing `neck_03`.
