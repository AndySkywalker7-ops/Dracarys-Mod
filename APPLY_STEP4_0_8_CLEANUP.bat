@echo off
setlocal
set "ROOT=%~dp0"

echo Dracarys Step 4.0.8 - removing obsolete rendering experiments...

del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\lod\FarDragonClientEvents.java" 2>nul
del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\lod\FarDragonLodProfile.java" 2>nul
del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\lod\FarDragonPresenceManager.java" 2>nul
del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\lod\FarDragonWorldRenderer.java" 2>nul
del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\model\lod\FarBalancedDragonModel.java" 2>nul
del /Q "%ROOT%src\main\java\com\dracarys\dracarysmod\client\renderer\layer\FarOpaquePresenceLayer.java" 2>nul
del /Q "%ROOT%src\main\resources\assets\dracarysmod\textures\entity\dragon\far_lod_neutral.png" 2>nul
del /Q "%ROOT%src\main\resources\assets\dracarysmod\textures\gui\far_dragon_silhouette.png" 2>nul

rmdir "%ROOT%src\main\java\com\dracarys\dracarysmod\client\lod" 2>nul
rmdir "%ROOT%src\main\java\com\dracarys\dracarysmod\client\model\lod" 2>nul
rmdir "%ROOT%src\main\java\com\dracarys\dracarysmod\client\renderer\layer" 2>nul

echo.
echo Cleanup complete. GitHub Desktop should show the obsolete files as deleted.
echo You can close this window.
pause
