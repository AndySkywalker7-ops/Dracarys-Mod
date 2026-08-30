# Dracarys Mod — Step 4.0.5
## Far Dragon Presence / LOD Foundation

### Why this exists
Testing with `F3+B` proved that the real entity and its hitbox disappear together at long range. At that point the client no longer owns/tracks the vanilla entity, so increasing renderer culling values cannot solve the problem.

### Phase A implemented here
This patch introduces a **client-side far-presence bridge**:

1. A live dragon is normally tracked and rendered by Minecraft.
2. When vanilla tracking removes that dragon from the client level, Dracarys captures its last visible state.
3. A visual-only proxy remains cached locally.
4. The proxy is rendered after particles even though the real entity is no longer present client-side.
5. If vanilla starts tracking the real dragon again, the proxy is removed immediately.

The proxy is **not added to the world** and therefore has:

- no hitbox;
- no AI;
- no pathfinding;
- no combat authority;
- no collision;
- no sounds/particles produced by ticking;
- no chunk forcing.

### Ranges in this foundation
- Juvenile: up to ~800 blocks
- Adolescent: up to ~1000 blocks
- Adult: up to ~1400 blocks
- Ancient: up to ~1800 blocks
- Colossal: up to ~2400 blocks

These distances are for the cached visual proxy, not for vanilla entity tracking.

### Important limitation of Step 4.0.5
The proxy uses the **last known position**. Once the real dragon stops being tracked, the proxy does not know where a moving dragon goes. This is intentional for the foundation test.

The next networking iteration will add server-fed `DragonPresenceSnapshot` updates and interpolation so distant flying dragons continue moving correctly without forcing chunks.

### Performance
This first proof uses the current BALANCED renderer to validate long-distance presence. It caches at most 32 dragons and expires entries after 20 minutes. A later step will replace the full proxy with a genuinely simplified LOD mesh.

### Test
1. Build the `dragon-visual-overhaul` branch.
2. Spawn an adult or colossal dragon.
3. Keep the dragon alive and move away until vanilla would normally remove it.
4. Verify that the visible dragon remains after `F3+B` would have lost the real hitbox.
5. Walk/fly back toward it and confirm the far proxy disappears when the real entity returns.

Recommended:

```text
/dracarys spawn white giant colossal
```
