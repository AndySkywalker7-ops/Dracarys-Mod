# Dracarys Mod — Step 4.0.7D
## True 3D Far Dragon LOD

### Decision

The Step 4.0.7C screen-space impostor is discarded.

It proved that anti-fog screen drawing can keep a silhouette visible, but it
looked like a HUD marker rather than a creature in the world.

This patch returns all long-range presence to world-space 3D.

### Visual pipeline

The distance bands are now centralized in `FarDragonLodProfile`.

Example for ADULT:

- 0–120 blocks: FULL articulated model
- 120–320 blocks: FAR_3D low-detail model
- 320–1400 blocks: VERY_FAR_3D model
- beyond 1400: no render

For COLOSSAL:

- 0–150: FULL
- 150–560: FAR_3D
- 560–2400: VERY_FAR_3D

### FAR_3D model

The low-detail mesh keeps:

- body;
- neck;
- head/snout;
- two large stepped 3D wings;
- two-segment tail;
- four simple legs.

The model is rendered with a neutral texture and tinted using the dragon's real
variant color.

Wings use a darker secondary tint so the far dragon is not one flat color.

### VERY_FAR_3D

Uses the same baked model but omits the four legs during rendering.

This retains the important silhouette while reducing geometry.

### Animation

The far model has intentionally cheap animation:

- slow wing flap;
- slight body flight pitch;
- simple tail sway;
- basic leg motion on the ground.

No new entity searches or AI work are performed.

### Performance

- one additional baked model layer;
- model instance is created lazily once;
- no geometry allocation per frame;
- no screen-space sprites;
- no GUI-pre world markers;
- no global `noCulling = true`.

### Existing long-distance fix retained

Minecraft was observed to stop drawing the normal dragon around 65–75 blocks,
even while the client still tracked it.

The proven manual bridge remains active from 40 blocks until the FULL model LOD
boundary.

After that, Dracarys draws only the dedicated low-detail model.

### Cleanup from previous experiments

The following old files may remain physically in the repository after copying
this patch, but they are no longer referenced:

- `client/renderer/layer/FarOpaquePresenceLayer.java`
- `assets/dracarysmod/textures/gui/far_dragon_silhouette.png`

They can be deleted later after this build is confirmed.

### Does NOT change

- growth scale;
- dragon dimensions;
- hitbox;
- AI;
- combat;
- tracking range;
- drops;
- taming.

### Test

Use one ADULT first.

Expected HUD:
- around 80 blocks: `Current LOD: FULL`
- around 150 blocks: `Current LOD: FAR_3D`
- beyond ~320 blocks: `Current LOD: VERY_FAR_3D`

Then test a COLOSSAL.

Evaluate:
1. Is it clearly 3D?
2. Does it rotate correctly?
3. Does it sit at the correct world position?
4. Does it retain a recognizable body/wing/ tail silhouette?
5. Does it hide behind terrain naturally?
6. Is the switch FULL -> FAR visually acceptable?

Do not evaluate the physical hitbox in this step. Multipart hitboxes remain the
next technical milestone.
