# Dracarys Mod — Step 4.0.9B
## Multipart Hitbox Model Alignment

### What was wrong

Step 4.0.9 proved that the seven Forge PartEntity zones correctly forwarded
hits/interactions to the parent dragon, but their layout used:

- `visualLength()` capped to 28 blocks;
- vertical positions derived partly from the much smaller physical `getBbHeight()`;
- generic normalized offsets rather than the actual BALANCED model landmarks.

The renderer, however, scales `BalancedDragonModel` with `renderScale()`.
For Giant/Colossal dragons this creates a very large gap between the visual mesh
and the interaction zones, leaving the multipart boxes under the dragon.

### Step 4.0.9B fix

The hitboxes are now calibrated in the same model-pixel coordinate system as
`BalancedDragonModel` (16 model pixels = one unscaled Minecraft block), then
multiplied by `renderScale()`.

Static model-space landmarks are centralized in:

`DragonMultipartLayout.java`

Zones:

- BODY — chest/torso envelope;
- NECK — articulated neck corridor;
- HEAD — skull + snout envelope;
- LEFT_WING / RIGHT_WING — broad wing envelopes;
- TAIL — long tail corridor;
- LEGS — fore/hind-leg vertical envelope.

### Dynamic wing alignment

Each wing remains one PartEntity, but its AABB follows the visual wing roll:

- idle wing pose;
- flying flap phase;
- downed pose.

The lateral projection shrinks as the wing rises and the vertical projection
grows, so the box follows the visible wing instead of staying at body height.

### Tail alignment

The tail receives a small lateral sweep matching the existing tail animation so
projectiles aimed at a visibly swaying tail remain inside the target zone.

### Broad-phase radius

Forge multipart discovery depends on the level's maximum entity search radius.
The radius is now raised only when the current dragon visual scale requires it,
with a hard ceiling of 112 blocks. This is necessary for the current enormous
Giant/Colossal wing geometry; it should be profiled in the large modpack after
functional validation.

### Explicitly unchanged

Step 4.0.8 long-range rendering was not redesigned or replaced.

Unchanged:

- DracarysDragonRenderer logic;
- normal/fallback full-model authority;
- textures;
- culling policy;
- AI;
- movement;
- taming;
- damage forwarding;
- non-colliding PartEntity behavior.

Only the multipart layout/calibration changed (plus the debug HUD version label).

### Test

1. Enable F3+B.
2. Spawn a normal adult.
3. Verify HEAD/NECK/BODY/WINGS/TAIL/LEGS overlap the visible anatomy.
4. Shoot arrows at head, wing and tail.
5. Spawn Giant Colossal and repeat.
6. Verify the boxes scale upward/outward with the rendered model instead of
   collecting under the feet.
7. Re-check long-range visibility to confirm Step 4.0.8 remains unchanged.
