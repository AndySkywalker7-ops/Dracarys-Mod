# STEP 5.10R3 — Conceptual Vertex / Region Changes

The authored asset is procedurally reproducible from `tools/generate_head_asset_r3.py`. Changes are applied by anatomical region rather than arbitrary decoration.

## Primary skull shell
- Added one posterior collar cross-section.
- Expanded posterior and temporal cross-sections.
- Rebalanced orbital cross-section.
- Repositioned and narrowed all forward snout cross-sections.
- Reduced the final nasal cross-section.

## Brow wedges
- Posterior brow vertices moved laterally outward and dorsally upward.
- Forward brow vertices moved rearward to shorten the visible facial bar.
- Lower brow surface moved closer to the eye to form a stronger socket roof.

## Cheek / temporal wedges
- Posterior vertices expanded laterally and ventrally.
- Forward vertices remain narrower to preserve wedge-shaped facial taper.

## Eye quads
- Lateral placement preserved.
- Longitudinal station moved rearward relative to the newly shortened snout.

## Nostril quads
- Reduced width/height.
- Moved medially and rearward with the new nasal tip.

## Horn chains
- Main horn root remains embedded in the cranium.
- Mid/distal control points moved rearward and slightly outward.
- Secondary and temporal horns were adjusted to follow the larger rear cranium.

## Upper teeth
- Root stations follow the shorter upper jaw.
- Radius and exposed length reduced.
- Front fangs remain larger than secondary teeth.

## Lower jaw shell
- Posterior rings widened/deepened.
- Forward rings narrowed and shortened.
- All jaw vertices receive a -1.15 Y authored correction in runtime coordinate space to close idle gape while retaining the existing jaw bone.

## Lower teeth / tongue
- Longitudinal stations shortened with the jaw.
- Teeth reduced and redistributed.
- Tongue ribbon shortened to remain inside the new mandibular envelope.

## Explicit non-changes
No body, neck rig, wing, leg, tail, renderer-range, entity, hitbox, or gameplay vertices/code are edited by this stage.
