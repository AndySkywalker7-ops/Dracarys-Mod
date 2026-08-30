# Dracarys Mod — Step 4.0.7B
## Opaque Far Presence

### Observed problem

Long-range rendering now works, but a dragon can still look almost transparent
when its background is only bright sky/fog. When terrain is behind it, its real
color becomes much easier to see.

The previous outline pass only reinforced the edge of the dragon. It did not
give visual mass back to the interior of the model.

### New solution

The outline pass is removed.

At 90 blocks and farther, Dracarys now adds an opaque reinforcement layer:

- same dragon geometry;
- same variant texture;
- full alpha;
- full light;
- entityCutoutNoCullZOffset render type;
- subtle neutral darkening for contrast.

This is a renderer layer, which means it automatically follows the exact
animation, rotation and scale already calculated by the main dragon renderer.

### Important

It does NOT:
- increase visual size;
- change renderScale;
- change hitbox;
- change AI;
- change combat;
- change growth;
- change tracking.

### Test

Test especially against pure sky:

1. white dragon at 100–150 blocks;
2. blue/green dragon at the same distance;
3. compare with terrain behind the dragon and then only sky behind it.

Success means the dragon retains a visibly opaque body and its dominant color
instead of becoming a washed-out transparent silhouette.
