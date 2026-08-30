#!/usr/bin/env sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

rm -f \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/lod/FarDragonClientEvents.java" \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/lod/FarDragonLodProfile.java" \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/lod/FarDragonPresenceManager.java" \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/lod/FarDragonWorldRenderer.java" \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/model/lod/FarBalancedDragonModel.java" \
  "$ROOT/src/main/java/com/dracarys/dracarysmod/client/renderer/layer/FarOpaquePresenceLayer.java" \
  "$ROOT/src/main/resources/assets/dracarysmod/textures/entity/dragon/far_lod_neutral.png" \
  "$ROOT/src/main/resources/assets/dracarysmod/textures/gui/far_dragon_silhouette.png"

rmdir "$ROOT/src/main/java/com/dracarys/dracarysmod/client/lod" 2>/dev/null || true
rmdir "$ROOT/src/main/java/com/dracarys/dracarysmod/client/model/lod" 2>/dev/null || true
rmdir "$ROOT/src/main/java/com/dracarys/dracarysmod/client/renderer/layer" 2>/dev/null || true

echo "Dracarys Step 4.0.8 cleanup complete."
