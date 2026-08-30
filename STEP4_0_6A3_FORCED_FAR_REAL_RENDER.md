# Dracarys Mod — Step 4.0.6A3
## Forced Far Real-Entity Rendering

### Why another fix is necessary

The Step 4.0.6A diagnostics showed a colossal dragon at roughly 798 blocks with:

- Real entity tracked: YES
- LOD cached: YES
- LOD active: NO
- Tracking lost: never

Yet the normal model was already disappearing.

That means the dragon is still present in ClientLevel, but Minecraft's normal
world entity rendering path is not drawing it.

### New strategy

This patch no longer relies on the normal entity pass once the dragon is far
away.

At 560 blocks and beyond, while the real dragon still exists in ClientLevel,
Dracarys manually calls its EntityRenderer from RenderLevelStageEvent.

This intentionally bypasses the normal LevelRenderer entity-distance decision.

There is intentional overlap from 560 blocks to the point where vanilla would
normally stop drawing the dragon. This prevents a visual pop/gap.

### Rendering phases

Near:
- vanilla real entity renderer.

Far but still tracked:
- manual direct renderer of the REAL entity.

No longer tracked:
- cached far LOD/proxy.

### What does NOT change

- dragon visual size;
- growth stages;
- physical hitbox;
- AI;
- combat;
- damage;
- pathfinding;
- tracking range.

### Test

Use the debug HUD.

Past 560 blocks it should show:

Forced real render active: YES
Forced real attempts: increasing

If those attempts increase, the manual bridge is executing.

The dragon should remain visible beyond the previous ~800 block cutoff for as
long as the real entity remains present in ClientLevel.
