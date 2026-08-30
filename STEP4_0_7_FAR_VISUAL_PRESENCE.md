# Dracarys Mod — Step 4.0.7
## Far Visual Presence

The previous test confirmed the manual real-entity bridge is working:

- Forced real render active: YES
- Forced real attempts: increasing
- the dragon remains visible well beyond the old ~65–75 block cutoff.

The remaining issue is readability. At long distance a dragon can blend into:
- sky;
- shader fog;
- snow;
- water glare;
- bright terrain.

## Change

At 90 blocks and beyond, Dracarys adds a second visual-only outline pass using
Minecraft's OutlineBufferSource.

This pass:
- does NOT enlarge the dragon;
- does NOT change renderScale;
- does NOT alter the hitbox;
- does NOT change AI or gameplay;
- keeps the normal textured dragon render underneath;
- adds a dark neutral contour so wings/body/head remain readable as a silhouette.

The same contour is used for the cached far LOD if the real entity eventually
leaves ClientLevel.

## Test

Use a white or light-colored dragon first because it is the hardest case.

Expected:
- under 90 blocks: normal appearance;
- over 90 blocks: same physical/visual size, but a clearer silhouette;
- at 120–200+ blocks: wings and body should remain easier to identify against sky/fog.

If the outline is too strong, too dark or too game-like, its opacity/color can
be reduced after the test without touching the long-range rendering system.
