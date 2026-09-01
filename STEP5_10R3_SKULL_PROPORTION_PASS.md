# STEP 5.10R3 — Skull Proportion & Silhouette Pass

## Status
Implemented as an authored-mesh refinement of STEP 5.10R2. The Java renderer/loader architecture is intentionally unchanged. No CubeListBuilder skull geometry was reintroduced.

## Scope lock
This pass changes only the authored head asset geometry and its asset manifest. Body, wings, legs, tail, entity behavior, long-range rendering, culling, tracking, multipart architecture, stages, size tiers, AI, combat, taming, ownership, spawn, worldgen, and navigation are untouched.

## Primary geometry changes

### Snout
- Orbital-to-nose reference span: 16.15 model px (R2) -> 11.85 model px (R3), approximately -26.6%.
- Snout stations were redistributed rather than translating the nose as one rigid piece.
- Width now tapers more aggressively from orbital region to the nasal tip.

### Rear skull / cranium
- Maximum primary-shell lateral extent: ~8.085 half-width (R2) -> ~10.092 (R3), approximately +24.8%.
- Growth is concentrated posterior to the eyes.
- Skull height changed only modestly; the expansion is primarily lateral/posterior.

### Nose
- Final primary-shell half-width: 2.55 -> 2.15, approximately -15.7%.
- Final primary-shell half-height: 1.65 -> 1.35, approximately -18.2%.
- Nostrils were moved inward and reduced with the nasal taper.

### Temporal / cheek region
- Maximum authored cheek lateral point: 7.00 -> 8.35, approximately +19.3%.
- Rear jaw-supporting cheek volume was deepened to make the eye/temporal region dominate over the snout.

### Orbits / brow
- Eyes remain lateral/anterolateral.
- Eye station moved from Z=-7.9 to Z=-6.8 in authored runtime coordinates.
- Brow overhang was enlarged and lowered over the orbit.
- Cheek and rear orbital masses now surround the eye more strongly so the eye sits inside the skull instead of reading as a surface decal.

### Jaw
- Rear jaw half-width: 5.65 -> 6.30 (+11.5%).
- Jaw authored longitudinal span: ~19.0 -> ~16.25 model px (-14.5%).
- Rear jaw depth was increased while the front tapers much more strongly.
- Entire authored lower-jaw mesh was raised by 1.15 model px relative to the existing jaw bone, closing the neutral mouth seam without changing animation code or pivots.

### Teeth
- Tooth radii and lengths were reduced substantially, with main fangs retained as the dominant teeth.
- Teeth are designed to be partially hidden when the jaw is in its neutral pose rather than forming a permanent white saw edge.

### Main horns
- Existing concept retained.
- Root thickness increased slightly.
- Distal points were moved farther rearward to create visibly stronger backward curvature.
- No new decorative horn families were added.

### Skull -> neck bridge
- Added a new authored rear collar station extending the skull approximately 3 model px farther toward the neck.
- This is intentionally only an occipital transition, not STEP 5.11 neck reconstruction.

## Geometry counts
- Skull: 662 vertices / 748 triangles.
- Jaw: 241 vertices / 272 triangles.

## Texture / UV
The existing 15 variant textures are byte-identical to STEP 5.10R2. Existing UV regions were preserved. This stage does not perform micro-surface texture polish.

## Java
No Java source file was changed in STEP 5.10R3. The following R2 code remains byte-identical:
- AuthoredDragonHeadMesh.java
- BalancedDragonModel.java
- DracarysHeadAssetLayer.java
- DracarysDragonRenderer.java

## Visual approval
Offline turnaround previews are included for geometry sanity and R2/R3 comparison. They are not a substitute for Minecraft visual acceptance. STEP 5.10R3 should remain unapproved until the required in-game views are captured.
