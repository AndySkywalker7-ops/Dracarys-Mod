# Dracarys Mod — Step 4.0.6A4
## Corrected Real-Render Bridge Threshold

### Diagnosis

The Step 4.0.6A3 HUD revealed the actual cutoff is around tens of blocks,
not hundreds:

- Distance: ~67 blocks
- Manual bridge starts: 560 blocks
- Forced real render active: NO
- Forced real attempts: 0

Therefore the manual render bridge was configured far beyond the point at
which the normal dragon model had already disappeared.

### Fix

The forced real-entity render bridge now starts at:

40 blocks

This deliberately overlaps the normal vanilla rendering zone before the
observed ~65-75 block cutoff.

Expected transition:

0–40 blocks
- vanilla renderer only

40–~65/75 blocks
- vanilla renderer + Dracarys manual bridge overlap

after vanilla stops drawing the normal entity
- Dracarys manual bridge continues drawing the real tracked entity

when the real entity finally leaves ClientLevel
- Far Dragon LOD can take over

### Goal of this test

Do not evaluate anatomy or hitboxes in this build.

The test is successful if the colossal remains visible at least 50 blocks
farther than the previous ~65-75 block cutoff.

Target minimum:
~120 blocks from the dragon entity origin.

### No gameplay changes

This patch does not change:
- dragon size;
- growth;
- hitbox;
- AI;
- combat;
- pathfinding;
- tracking range.
