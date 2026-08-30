# Dracarys Mod — Step 4.0.9
## Multipart Dragon Hitbox Foundation

### Goal

Add real multipart interaction/damage zones without changing the stable Step 4.0.8 long-range renderer, visual scale, AI, attributes or parent collision box.

### Parts

Seven Forge `PartEntity` zones are created in a stable order:

1. body
2. head
3. neck
4. left_wing
5. right_wing
6. tail
7. legs

All parts are:
- pickable / targetable;
- damage-forwarding to the parent dragon;
- interaction-forwarding to the parent dragon;
- non-persistent;
- non-colliding and `noPhysics`, so wings and tail do not behave as giant invisible walls.

### ID synchronization

The parent overrides `setId(int)` and assigns the seven child IDs sequentially. This follows the multipart pattern used by the vanilla Ender Dragon and allows Forge's client/server multipart maps to refer to the same parts.

### Physical coverage policy

The multipart span follows `visualLength()` but is capped at 28 blocks in this foundation. This is deliberate: exact wing-tip coverage on the largest visual dragons would require a very large global entity-query radius and should be profiled before deployment in a 300–600 mod pack.

Forge's max entity query radius is raised only to 18 blocks. That is enough for every part in this bounded foundation while avoiding a multi-dozen-block scan radius.

### Zone proportions

The seven AABBs rotate with the dragon's yaw and are rebuilt every tick on both client and server. Each local oriented prism is converted to the world-axis AABB Minecraft uses for hit tests.

- BODY: central chest/torso
- NECK: forward upper body
- HEAD: muzzle/head area
- LEFT/RIGHT WING: lateral interaction zones
- TAIL: rear longitudinal zone
- LEGS: lower body / four-leg region

### Damage policy

Step 4.0.9 does not rebalance combat. Every part forwards 100% of incoming damage to `DracarysDragonEntity.hurt(...)`, preserving downed/taming logic. Part-specific multipliers can be added later.

### F3+B debug

Because vanilla debug rendering is not guaranteed to visualize arbitrary Forge `PartEntity` instances, Dracarys explicitly draws the actual multipart AABBs while Minecraft hitbox debug is enabled.

Colors:
- BODY: yellow
- HEAD: red
- NECK: orange
- LEFT WING: cyan
- RIGHT WING: blue
- TAIL: purple
- LEGS: green

The HUD now says `DRACARYS RENDER DEBUG - STEP 4.0.9` and reports `Multipart hitboxes: 7 | F3+B: ON/OFF`.

### Explicitly unchanged

- Step 4.0.8 long-range rendering
- `DracarysDragonRenderer`
- normal dragon textures
- `renderScale()`
- growth stages / visual sizes
- parent `EntityDimensions`
- AI/pathfinding
- health/damage balance
- tracking distance
- no LOD, no outline, no fog hacks

### Manual in-game validation

1. Spawn an Adult and a Colossal.
2. Press F3+B.
3. Confirm seven colored zones follow the anatomy as the dragon rotates/moves.
4. Hit head, body, wing and tail separately; parent health should decrease.
5. Right-click a valid interactive part; parent interaction should run.
6. Walk near wings/tail; they must NOT act as solid walls.
7. Re-test Step 4.0.8 long-range visibility to confirm there is no regression.

### Build status

Static validation is included. A successful static scan is not a Forge compile or an in-game test. GitHub Actions remains the real compile step for this project.
