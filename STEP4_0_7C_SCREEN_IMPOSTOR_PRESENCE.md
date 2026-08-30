# Dracarys Mod — Step 4.0.7C
## Screen-space Anti-Fog Dragon Presence

### Why this exists

The 3D long-range renderer is working, but shader fog can still wash the dragon
into the sky. An opaque world-space render layer was not sufficient because the
shader/composite stage can still attenuate distant world geometry.

### New approach

At 120 blocks and farther, Dracarys adds a screen-space dragon impostor:

- rendered after the world, before normal HUD elements;
- therefore not erased by world fog;
- dominant color follows the real dragon variant;
- dark border preserves silhouette;
- apparent size still decreases with distance;
- no change to dragon renderScale;
- no change to hitbox or gameplay.

### Terrain occlusion

The impostor is NOT blindly drawn through mountains.

A client-side block ray test is refreshed approximately twice per second.
If terrain blocks the line from camera to the dragon body, the impostor is not
shown.

### This is a LOD, not a replacement model

Near dragons still use the full 3D renderer.

At long range:
1. the real 3D dragon continues rendering where possible;
2. the screen-space silhouette reinforces visibility against sky/fog.

This is similar to an impostor LOD used by large open-world games.

### Test

Recommended:
- Adult at 120, 160, 200+ blocks.
- Colossal at 150, 250, 350+ blocks.
- Test with only sky behind the dragon.
- Then place a mountain between player and dragon to verify the impostor hides.

HUD success indicators:
- Screen impostor active: YES
- Line of sight clear: YES
- Screen-impostor frames: increasing
