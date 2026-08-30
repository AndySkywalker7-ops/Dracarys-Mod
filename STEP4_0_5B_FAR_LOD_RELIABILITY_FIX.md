# Dracarys Mod — Step 4.0.5B
## Far Dragon LOD Reliability Fix

### Why Step 4.0.5 did not work reliably

The first LOD foundation depended on `EntityLeaveLevelEvent` to create the
far-dragon cache only after vanilla stopped tracking the dragon.

The in-game test showed the real dragon and its F3+B hitbox disappearing at
the normal tracking boundary without a visible LOD takeover.

### New strategy

The cache now exists BEFORE the dragon leaves vanilla tracking:

1. EntityJoinLevelEvent -> create the far-presence entry immediately.
2. Client ticks -> refresh position/rotation while vanilla still knows the dragon.
3. Render stage -> while the real entity exists, render nothing extra.
4. Vanilla tracking drops the entity -> `ClientLevel#getEntity(id)` returns null.
5. The cached proxy immediately becomes visible at the last known position.

`EntityLeaveLevelEvent` remains only as a best-effort final refresh. The LOD
no longer depends on that event firing.

### Important limitations

This is still the foundation version:
- the far dragon freezes at its last known position after tracking is lost;
- it uses the full dragon renderer rather than a cheap dedicated LOD mesh;
- it does not force chunks;
- it has no hitbox, AI, collision, combat or gameplay authority.

### Separate issue discovered during testing

The F3+B physical hitbox is much smaller/lower than the enormous visual model.
That is independent from the long-range LOD problem and comes from Dracarys
intentionally scaling the visual dragon much more aggressively than its
physical EntityDimensions.

Do not solve that by making the hitbox as large as the full visual model.
It needs a separate gameplay-safe dimensions/anchor pass after long-range
presence is confirmed.
