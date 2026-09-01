# STEP 5.10R — In-game visual acceptance

1. Apply the patch over the current branch.
2. Run GitHub Actions / Forge build.
3. Spawn a medium adult first so head proportions are easy to judge.
4. Use the same dragon/variant for every screenshot.
5. Capture exactly:
   - FRONT
   - LEFT
   - RIGHT
   - TOP
   - BOTTOM
   - REAR
   - 3/4 FRONT LEFT
   - 3/4 FRONT RIGHT
   - 3/4 with jaw open / bite animation
6. Confirm that the base cuboid skull from STEP 5.10 is gone.
7. Confirm eyes are lateral/anterolateral and sit in sockets.
8. Confirm horns emerge from the skull with no floating roots.
9. Confirm the jaw opens around its posterior hinge and teeth remain attached.
10. Repeat with a giant/colossal dragon to confirm scaling does not introduce gaps.
11. Move far enough away to verify the head remains present under the already-approved long-range renderer.

Automatic FAIL if the visible head still reads as stacked boxes, if the dedicated texture is missing/purple-black, or if a second full dragon render appears.
