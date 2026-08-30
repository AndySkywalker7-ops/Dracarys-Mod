# Dracarys Mod — Step 4.0.6A2
## Real Renderer Range Fix

### What the diagnostic HUD proved

During the failing long-range test, the HUD showed approximately:

- `Real entity tracked: YES`
- `LOD cached: YES`
- `LOD eligible/active: NO`
- `Distance: ~798 / 2400 blocks`
- `Render attempts: 0`
- `Tracking lost: never`

This proves the client still had the real dragon entity even though the normal
model (and F3+B debug rendering) was no longer visible.

Therefore the first disappearance was NOT a network tracking boundary.

It was a normal entity-renderer culling/range decision.

### Fix

`DracarysDragonRenderer` now overrides `shouldRender(...)`.

For Dracarys dragons the renderer:

1. uses a Dracarys-specific finite maximum distance;
2. does not rely on vanilla's ordinary entity render-distance cutoff;
3. still respects camera frustum culling using the enlarged visual culling box;
4. does not set `noCulling = true`;
5. does not alter AI, hitbox, scale, gameplay or server tracking.

### Expected behavior

While the dragon is still present in ClientLevel:

`Real entity tracked: YES`
-> real full model should continue rendering.

Only after vanilla actually removes the entity:

`Real entity tracked: NO`
-> far-LOD becomes eligible and takes over.

### Hitbox note

The small F3+B physical hitbox under the colossal dragon is a separate issue.
This patch intentionally does not change it.

That will be addressed in the multipart hitbox phase after long-range rendering
is confirmed.
