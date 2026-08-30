# Dracarys Mod — Step 4.0.8
## Clean Long Range Dragon Rendering

### Objective

One real entity. One full model. One normal variant texture. One visual representation per frame.

### Audit result

The previous branch contained multiple possible visual routes:

1. `DracarysDragonRenderer` — normal FULL model.
2. `FarDragonPresenceManager` — manual render route.
3. `FarDragonWorldRenderer` — alternate FAR/VERY_FAR model.
4. `FarBalancedDragonModel` — second dragon mesh.
5. `FarOpaquePresenceLayer` — second render of the FULL mesh.
6. old screen-impostor assets and GUI experiments.
7. Step 4.0.7E fog/outline reinforcement.

Those routes could overlap and were the source of duplicates, dark copies and abrupt visual changes.

### Step 4.0.8 architecture

#### Primary path

`DracarysDragonRenderer`
→ `BalancedDragonModel`
→ normal `textures/entity/dragon/[variant].png`

`shouldRender(...)` now uses:
- practical custom distance by growth stage and size tier;
- the visual-only `getBoundingBoxForCulling()`;
- normal frustum visibility.

#### Compatibility fallback

A large modpack may contain entity-culling/render optimization code that skips the normal entity pass even while the real entity is still present client-side.

`CleanLongRangeDragonRenderEvents` therefore keeps references only to REAL client-tracked Dracarys dragons. It does not create proxies.

Each frame:

1. the normal renderer gets first opportunity;
2. `DracarysDragonRenderer.render(...)` records that the real dragon was drawn;
3. at `AFTER_ENTITIES`, the fallback checks the real tracked dragons;
4. if a dragon was already rendered this frame: do nothing;
5. if it was skipped but is still in custom range + frustum: invoke the SAME registered renderer exactly once.

Therefore there is no FULL + FAR combination and no second visual representation.

### Full-model range targets

Approximate configured ceilings before client/chunk tracking limits:

- Baby: 128 blocks
- Juvenile: 192 blocks
- Adolescent: 256 blocks
- Adult: 320 blocks
- Ancient: 448 blocks
- Colossal: 512 blocks

Size tier applies a modest multiplier; the hard cap is 640 blocks.

`clientTrackingRange` is reduced from the experimental 96 chunks to 40 chunks, which is still sufficient for the 640-block render ceiling and is more appropriate for large modpacks.

### Culling vs hitbox

`getBoundingBoxForCulling()` is render-only and follows visual size.

`EntityDimensions` remains unchanged and continues to control gameplay/physical collision. Multipart hitboxes are NOT part of Step 4.0.8.

### Deleted experiments

Run `APPLY_STEP4_0_8_CLEANUP.bat` after copying this patch into the repository. It removes:

- FarDragonClientEvents
- FarDragonLodProfile
- FarDragonPresenceManager
- FarDragonWorldRenderer
- FarBalancedDragonModel
- FarOpaquePresenceLayer
- far_lod_neutral.png
- far_dragon_silhouette.png

### Debug HUD

Temporary text-only debug remains. It does not render any dragon in GUI space.

It shows:

- Stage
- Size
- Distance
- Real entity tracked
- Renderer shouldRender
- Inside custom distance
- Frustum visible
- Culling AABB
- Render calls
- Authority this frame: `NORMAL_FULL`, `FALLBACK_FULL`, or `NONE`

### Expected test

Spawn:

`/dracarys spawn white giant adult`

Check approximately 50, 100, 150, 200 and 250 blocks.

At every visible distance the appearance must remain the same `BalancedDragonModel` with `white.png`.

Then test:

`/dracarys spawn white giant colossal`

No alternate model, outline, black copy, ghost or sprite should appear.

### Validation status

Static project validation passed: 424 checks, 0 errors, 0 warnings.

A real Forge/Gradle build was not executable in the artifact environment because Gradle 8.8 and the Forge dependency cache are not installed and outbound network access is disabled. Therefore GitHub Actions remains the required compilation validation.

Compilation success is not equivalent to an in-game test.
