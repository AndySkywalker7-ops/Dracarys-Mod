# STEP 5.10R2 — `code too large` hotfix

## Failure reproduced
GitHub Actions failed in `AuthoredDragonHeadMesh.java` at the large
`SKULL_VERTICES` static array initializer with:

`error: code too large`

## Root cause
The STEP 5.10R generated mesh was authored correctly as polygon data, but the
runtime copy of that data was embedded as very large Java array literals.
`javac` translates those literals into bytecode in the class static initializer
(`<clinit>`). The combined initializer exceeded the JVM method bytecode limit.

This was a packaging error, not an anatomical/modeling error.

## Fix
Mesh geometry is no longer embedded in Java source.

Runtime geometry now lives in:

`assets/dracarysmod/models/entity/dragon/head/dracarys_head.mesh`

`AuthoredDragonHeadMesh` lazily loads and validates that binary resource the
first time the authored head is rendered.

The editable source assets remain:

- `dracarys_head.obj`
- `dracarys_head.mtl`
- `dracarys_head.asset.json`

## Binary format
- magic: `DRHM`
- version: `1`
- skull: vertex float stream, triangle indices, triangle-normal stream
- jaw: vertex float stream, triangle indices, triangle-normal stream

Vertex stream remains `X Y Z U V`.

## Geometry preserved
No authored vertex, triangle, UV, or normal data was intentionally changed.

- skull vertex float count: 3265
- skull vertex count: 653
- skull triangle count: 732
- jaw vertex float count: 1205
- jaw vertex count: 241
- jaw triangle count: 272

## Scope
This hotfix only changes the mesh storage/loader.

It does not change:
- dragon anatomy
- head proportions
- textures
- renderer distance/culling logic
- entity logic
- multipart hitboxes
- AI/gameplay
