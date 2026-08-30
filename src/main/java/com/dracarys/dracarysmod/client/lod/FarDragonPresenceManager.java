package com.dracarys.dracarysmod.client.lod;

import com.dracarys.dracarysmod.DracarysMod;
import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Step 4.0.7C — Screen-space far dragon impostor.
 *
 * We keep the working 3D manual render bridge, but long-range shader fog can
 * still wash the dragon into the sky. The screen impostor is rendered AFTER
 * the world, so its silhouette and dominant color remain readable.
 *
 * Important:
 * - it does NOT change dragon size;
 * - it does NOT change hitboxes;
 * - it does NOT replace the real dragon at normal range;
 * - it is only a far visual aid / LOD;
 * - it performs a client ray test so the impostor does not show through terrain.
 */
public final class FarDragonPresenceManager {
    private static final int MAX_ENTRIES = 32;
    private static final long ENTRY_TTL_MS = 10L * 60L * 1000L;
    private static final int FULL_SNAPSHOT_REFRESH_TICKS = 20;

    private static final double FORCED_REAL_RENDER_START = 40.0D;

    /**
     * Start screen-space visibility reinforcement where shader fog becomes
     * visually destructive in the real tests.
     */
    private static final double SCREEN_IMPOSTOR_START = 120.0D;

    /**
     * Do not raycast every frame. 10 client ticks = roughly twice per second.
     */
    private static final int OCCLUSION_REFRESH_TICKS = 10;

    private static final ResourceLocation FAR_SILHOUETTE =
            DracarysMod.id("textures/gui/far_dragon_silhouette.png");

    private static final Map<UUID, Entry> ENTRIES = new LinkedHashMap<>();

    private static long renderStageCalls;
    private static long forcedRealRenderAttempts;
    private static long lodRenderAttempts;
    private static long impostorFrames;

    private FarDragonPresenceManager() {}

    public static void observe(DracarysDragonEntity dragon, Level level) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!level.isClientSide
                || minecraft.level != level
                || minecraft.player == null) {
            return;
        }

        if (!dragon.isAlive() || dragon.isDeadOrDying()) {
            forget(dragon.getUUID());
            return;
        }

        if (dragon.getStage().ordinal() < DragonStage.JUVENILE.ordinal()) {
            return;
        }

        Entry entry = ENTRIES.get(dragon.getUUID());

        if (entry == null || !entry.dimension.equals(level.dimension())) {
            CompoundTag tag = dragon.saveWithoutId(new CompoundTag());

            DracarysDragonEntity proxy =
                    new DracarysDragonEntity(ModEntities.DRAGON.get(), level);

            proxy.load(tag);
            proxy.moveTo(
                    dragon.getX(),
                    dragon.getY(),
                    dragon.getZ(),
                    dragon.getYRot(),
                    dragon.getXRot()
            );
            proxy.setFlying(dragon.isFlying());
            proxy.tickCount = dragon.tickCount;

            entry = new Entry(
                    dragon.getUUID(),
                    dragon.getId(),
                    level.dimension(),
                    proxy,
                    dragon.position(),
                    dragon.getYRot(),
                    dragon.getXRot(),
                    System.currentTimeMillis(),
                    maxDistanceFor(dragon.getStage())
            );

            ENTRIES.put(dragon.getUUID(), entry);
            trimToLimit();
        } else {
            updateFromLive(entry, dragon, false);
        }
    }

    public static void clientTick() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null) {
            clear();
            return;
        }

        long now = System.currentTimeMillis();
        long gameTime = minecraft.level.getGameTime();

        Iterator<Entry> iterator = ENTRIES.values().iterator();

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (!minecraft.level.dimension().equals(entry.dimension)) {
                iterator.remove();
                continue;
            }

            Entity current = minecraft.level.getEntity(entry.entityId);

            boolean realPresent = current instanceof DracarysDragonEntity live
                    && live.getUUID().equals(entry.uuid)
                    && live.isAlive()
                    && !live.isDeadOrDying();

            if (realPresent) {
                DracarysDragonEntity live = (DracarysDragonEntity) current;

                if (!entry.realPresentLastTick) {
                    entry.trackingRestoredMs = now;
                }

                entry.realPresentLastTick = true;
                entry.ticksSinceFullRefresh++;

                boolean fullRefresh =
                        entry.ticksSinceFullRefresh >= FULL_SNAPSHOT_REFRESH_TICKS;

                updateFromLive(entry, live, fullRefresh);

                if (fullRefresh) {
                    entry.ticksSinceFullRefresh = 0;
                }
            } else {
                if (entry.realPresentLastTick) {
                    entry.trackingLostMs = now;
                }

                entry.realPresentLastTick = false;

                if (now - entry.lastSeenLiveMs > ENTRY_TTL_MS) {
                    iterator.remove();
                    continue;
                }
            }

            double distance =
                    minecraft.player.position().distanceTo(entry.position);

            if (distance >= SCREEN_IMPOSTOR_START
                    && gameTime - entry.lastOcclusionCheckTick
                    >= OCCLUSION_REFRESH_TICKS) {

                entry.lastOcclusionCheckTick = gameTime;
                entry.lineOfSightClear =
                        computeLineOfSight(minecraft, entry);
            }
        }
    }

    private static void updateFromLive(
            Entry entry,
            DracarysDragonEntity dragon,
            boolean fullRefresh
    ) {
        if (fullRefresh) {
            CompoundTag tag = dragon.saveWithoutId(new CompoundTag());
            entry.proxy.load(tag);
        }

        entry.entityId = dragon.getId();
        entry.position = dragon.position();
        entry.yRot = dragon.getYRot();
        entry.xRot = dragon.getXRot();
        entry.lastSeenLiveMs = System.currentTimeMillis();
        entry.maxDistance = maxDistanceFor(dragon.getStage());

        entry.proxy.moveTo(
                entry.position.x,
                entry.position.y,
                entry.position.z,
                entry.yRot,
                entry.xRot
        );

        entry.proxy.setFlying(dragon.isFlying());
        entry.proxy.tickCount = dragon.tickCount;
    }

    public static void forget(UUID dragonId) {
        ENTRIES.remove(dragonId);
    }

    public static void clear() {
        ENTRIES.clear();
    }

    private static DracarysDragonEntity getLiveEntity(
            Minecraft minecraft,
            Entry entry
    ) {
        if (minecraft.level == null) return null;

        Entity current = minecraft.level.getEntity(entry.entityId);

        if (current instanceof DracarysDragonEntity dragon
                && dragon.getUUID().equals(entry.uuid)
                && dragon.isAlive()
                && !dragon.isDeadOrDying()) {
            return dragon;
        }

        return null;
    }

    /**
     * Working manual 3D bridge from Step 4.0.6A4.
     */
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        renderStageCalls++;

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null) {
            clear();
            return;
        }

        if (ENTRIES.isEmpty()) return;

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();

        boolean renderedAny = false;

        for (Entry entry : ENTRIES.values()) {
            if (!minecraft.level.dimension().equals(entry.dimension)) {
                continue;
            }

            DracarysDragonEntity live = getLiveEntity(minecraft, entry);

            double distance =
                    Math.sqrt(entry.position.distanceToSqr(camera));

            boolean inRange = distance <= entry.maxDistance;

            entry.lastDistance = distance;
            entry.lastRealPresent = live != null;
            entry.lastForcedRealEligible =
                    live != null
                            && distance >= FORCED_REAL_RENDER_START
                            && inRange;

            if (entry.lastForcedRealEligible) {
                if (event.getFrustum().isVisible(
                        live.getBoundingBoxForCulling())) {

                    entry.forcedRealAttempts++;
                    forcedRealRenderAttempts++;
                    entry.lastForcedRealRenderDistance = distance;

                    renderEntityDirect(
                            minecraft,
                            live,
                            live.position(),
                            live.getYRot(),
                            camera,
                            event,
                            poseStack,
                            buffers
                    );

                    renderedAny = true;
                }

                continue;
            }

            if (live != null) {
                continue;
            }

            entry.lastLodEligible = inRange;

            if (!inRange) continue;

            if (!event.getFrustum().isVisible(
                    entry.proxy.getBoundingBoxForCulling())) {
                continue;
            }

            entry.proxy.tickCount++;
            entry.lodAttempts++;
            lodRenderAttempts++;
            entry.lastLodRenderDistance = distance;

            renderEntityDirect(
                    minecraft,
                    entry.proxy,
                    entry.position,
                    entry.yRot,
                    camera,
                    event,
                    poseStack,
                    buffers
            );

            renderedAny = true;
        }

        if (renderedAny) {
            buffers.endBatch();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void renderEntityDirect(
            Minecraft minecraft,
            DracarysDragonEntity dragon,
            Vec3 position,
            float yRot,
            Vec3 camera,
            RenderLevelStageEvent event,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers
    ) {
        poseStack.pushPose();

        poseStack.translate(
                position.x - camera.x,
                position.y - camera.y,
                position.z - camera.z
        );

        EntityRenderer renderer =
                minecraft.getEntityRenderDispatcher().getRenderer(dragon);

        int packedLight = renderer.getPackedLightCoords(
                dragon,
                event.getPartialTick()
        );

        renderer.render(
                dragon,
                yRot,
                event.getPartialTick(),
                poseStack,
                buffers,
                packedLight
        );

        poseStack.popPose();
    }

    /**
     * Step 4.0.7C screen-space LOD.
     *
     * Rendered in RenderGuiEvent.Pre, which occurs after the 3D world is already
     * present on screen. Therefore shader sky/fog can no longer erase this
     * silhouette.
     */
    public static void renderScreenPresence(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null
                || minecraft.player == null
                || ENTRIES.isEmpty()) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        Vec3 cameraPos = camera.getPosition();

        for (Entry entry : ENTRIES.values()) {
            if (!minecraft.level.dimension().equals(entry.dimension)) {
                continue;
            }

            double distance =
                    minecraft.player.position().distanceTo(entry.position);

            if (distance < SCREEN_IMPOSTOR_START
                    || distance > entry.maxDistance
                    || !entry.lineOfSightClear) {
                continue;
            }

            Vec3 target =
                    entry.position.add(
                            0.0D,
                            visualAnchorHeight(entry.proxy),
                            0.0D
                    );

            ScreenPoint screen =
                    projectToScreen(
                            minecraft,
                            gui,
                            camera,
                            cameraPos,
                            target
                    );

            if (screen == null) continue;

            int width =
                    projectedWidth(
                            minecraft,
                            gui,
                            entry.proxy,
                            screen.depth
                    );

            if (width < 12) continue;

            int height = Math.max(6, width / 2);

            int x = (int) Math.round(screen.x - width / 2.0D);
            int y = (int) Math.round(screen.y - height / 2.0D);

            if (x > gui.guiWidth() + width
                    || y > gui.guiHeight() + height
                    || x + width < -width
                    || y + height < -height) {
                continue;
            }

            float[] rgb = variantColor(entry.proxy);

            /*
             * First pass = dark silhouette border.
             * Second pass = dominant dragon color.
             *
             * Both are intentionally opaque enough to survive bright sky.
             */
            gui.setColor(0.055F, 0.065F, 0.085F, 0.96F);
            gui.blit(
                    FAR_SILHOUETTE,
                    x - 2,
                    y - 1,
                    0.0F,
                    0.0F,
                    width + 4,
                    height + 2,
                    128,
                    64
            );

            gui.setColor(rgb[0], rgb[1], rgb[2], 0.94F);
            gui.blit(
                    FAR_SILHOUETTE,
                    x,
                    y,
                    0.0F,
                    0.0F,
                    width,
                    height,
                    128,
                    64
            );

            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            entry.impostorFrames++;
            impostorFrames++;
        }
    }

    public static void renderDebugHud(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();

        int x = 8;
        int y = 44;
        int lineHeight = 10;

        Entry nearest = nearestEntry(minecraft);

        gui.fill(
                x - 4,
                y - 4,
                x + 335,
                y + 180,
                0xA0000000
        );

        draw(
                gui,
                minecraft,
                "DRACARYS LOD DEBUG - STEP 4.0.7C",
                x,
                y,
                0xFFFFC857
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Cache entries: " + ENTRIES.size(),
                x,
                y,
                ENTRIES.isEmpty() ? 0xFFFF5555 : 0xFF55FF55
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Render-stage calls: " + renderStageCalls,
                x,
                y,
                0xFFFFFFFF
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Forced-real attempts: " + forcedRealRenderAttempts,
                x,
                y,
                forcedRealRenderAttempts > 0
                        ? 0xFF55FFFF
                        : 0xFFFFAA00
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Screen-impostor frames: " + impostorFrames,
                x,
                y,
                impostorFrames > 0
                        ? 0xFF55FFFF
                        : 0xFFFFAA00
        );
        y += lineHeight;

        if (nearest == null) {
            draw(
                    gui,
                    minecraft,
                    "Nearest cached dragon: NONE",
                    x,
                    y,
                    0xFFFF5555
            );
            return;
        }

        DracarysDragonEntity live =
                getLiveEntity(minecraft, nearest);

        double distance =
                minecraft.player.position()
                        .distanceTo(nearest.position);

        boolean inRange =
                distance <= nearest.maxDistance;

        boolean forcedReal =
                live != null
                        && distance >= FORCED_REAL_RENDER_START
                        && inRange;

        boolean screenImpostor =
                distance >= SCREEN_IMPOSTOR_START
                        && inRange
                        && nearest.lineOfSightClear;

        draw(
                gui,
                minecraft,
                "Stage: " + nearest.proxy.getStage().name()
                        + "  Real tracked: " + yesNo(live != null),
                x,
                y,
                live != null ? 0xFF55FF55 : 0xFFFF5555
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Forced real active: " + yesNo(forcedReal),
                x,
                y,
                forcedReal ? 0xFF55FFFF : 0xFFFFAA00
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Screen impostor active: " + yesNo(screenImpostor),
                x,
                y,
                screenImpostor ? 0xFF55FFFF : 0xFFFFAA00
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "Line of sight clear: "
                        + yesNo(nearest.lineOfSightClear),
                x,
                y,
                nearest.lineOfSightClear
                        ? 0xFF55FF55
                        : 0xFFFF5555
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                String.format(
                        "Distance: %.1f / %.1f blocks",
                        distance,
                        nearest.maxDistance
                ),
                x,
                y,
                inRange ? 0xFFFFFFFF : 0xFFFF5555
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                String.format(
                        "Impostor starts: %.0f blocks",
                        SCREEN_IMPOSTOR_START
                ),
                x,
                y,
                0xFFAAAAAA
        );
        y += lineHeight;

        draw(
                gui,
                minecraft,
                "This dragon impostor frames: "
                        + nearest.impostorFrames,
                x,
                y,
                nearest.impostorFrames > 0
                        ? 0xFF55FFFF
                        : 0xFFAAAAAA
        );
    }

    private static boolean computeLineOfSight(
            Minecraft minecraft,
            Entry entry
    ) {
        if (minecraft.level == null || minecraft.player == null) {
            return false;
        }

        Vec3 start =
                minecraft.gameRenderer.getMainCamera().getPosition();

        Vec3 end =
                entry.position.add(
                        0.0D,
                        visualAnchorHeight(entry.proxy),
                        0.0D
                );

        BlockHitResult hit =
                minecraft.level.clip(
                        new ClipContext(
                                start,
                                end,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                minecraft.player
                        )
                );

        if (hit.getType() == HitResult.Type.MISS) {
            return true;
        }

        double hitDistance = start.distanceTo(hit.getLocation());
        double dragonDistance = start.distanceTo(end);

        /*
         * Allow the ray to hit terrain immediately beneath/behind the dragon
         * without considering the dragon itself occluded.
         */
        return hitDistance >= dragonDistance - 6.0D;
    }

    private static ScreenPoint projectToScreen(
            Minecraft minecraft,
            GuiGraphics gui,
            Camera camera,
            Vec3 cameraPos,
            Vec3 target
    ) {
        double dx = target.x - cameraPos.x;
        double dy = target.y - cameraPos.y;
        double dz = target.z - cameraPos.z;

        Vector3f forward = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        Vector3f left = camera.getLeftVector();

        double depth =
                dx * forward.x()
                        + dy * forward.y()
                        + dz * forward.z();

        if (depth <= 1.0D) {
            return null;
        }

        double horizontal =
                -(dx * left.x()
                        + dy * left.y()
                        + dz * left.z());

        double vertical =
                dx * up.x()
                        + dy * up.y()
                        + dz * up.z();

        double fovDegrees =
                minecraft.options.fov().get();

        double focal =
                gui.guiHeight()
                        / (2.0D
                        * Math.tan(
                                Math.toRadians(fovDegrees) / 2.0D
                        ));

        double screenX =
                gui.guiWidth() / 2.0D
                        + horizontal / depth * focal;

        double screenY =
                gui.guiHeight() / 2.0D
                        - vertical / depth * focal;

        return new ScreenPoint(
                screenX,
                screenY,
                depth
        );
    }

    private static int projectedWidth(
            Minecraft minecraft,
            GuiGraphics gui,
            DracarysDragonEntity dragon,
            double depth
    ) {
        double nominalWorldWidth =
                nominalWorldWidth(dragon);

        double fovDegrees =
                minecraft.options.fov().get();

        double focal =
                gui.guiHeight()
                        / (2.0D
                        * Math.tan(
                                Math.toRadians(fovDegrees) / 2.0D
                        ));

        int projected =
                (int) Math.round(
                        nominalWorldWidth / depth * focal
                );

        return Math.max(
                16,
                Math.min(260, projected)
        );
    }

    private static double nominalWorldWidth(
            DracarysDragonEntity dragon
    ) {
        double stageWidth = switch (dragon.getStage()) {
            case BABY -> 7.0D;
            case JUVENILE -> 16.0D;
            case ADOLESCENT -> 24.0D;
            case ADULT -> 38.0D;
            case ANCIENT -> 52.0D;
            case COLOSSAL -> 76.0D;
        };

        double sizeFactor = switch (dragon.getSizeTier()) {
            case SMALL -> 0.85D;
            case MEDIUM -> 1.00D;
            case LARGE -> 1.18D;
            case GIANT -> 1.42D;
        };

        return stageWidth * sizeFactor;
    }

    private static double visualAnchorHeight(
            DracarysDragonEntity dragon
    ) {
        double stageHeight = switch (dragon.getStage()) {
            case BABY -> 1.5D;
            case JUVENILE -> 3.0D;
            case ADOLESCENT -> 4.5D;
            case ADULT -> 7.0D;
            case ANCIENT -> 10.0D;
            case COLOSSAL -> 15.0D;
        };

        double sizeFactor = switch (dragon.getSizeTier()) {
            case SMALL -> 0.85D;
            case MEDIUM -> 1.00D;
            case LARGE -> 1.18D;
            case GIANT -> 1.42D;
        };

        return stageHeight * sizeFactor;
    }

    private static float[] variantColor(
            DracarysDragonEntity dragon
    ) {
        return switch (dragon.getVariant().id()) {
            case "black" ->
                    new float[]{0.18F, 0.20F, 0.24F};
            case "white" ->
                    new float[]{0.82F, 0.84F, 0.88F};
            case "gray" ->
                    new float[]{0.46F, 0.49F, 0.54F};
            case "red" ->
                    new float[]{0.70F, 0.14F, 0.11F};
            case "crimson" ->
                    new float[]{0.53F, 0.08F, 0.12F};
            case "orange" ->
                    new float[]{0.82F, 0.35F, 0.08F};
            case "gold" ->
                    new float[]{0.78F, 0.56F, 0.13F};
            case "green" ->
                    new float[]{0.25F, 0.55F, 0.20F};
            case "dark_green" ->
                    new float[]{0.12F, 0.34F, 0.22F};
            case "blue" ->
                    new float[]{0.13F, 0.32F, 0.72F};
            case "dark_blue" ->
                    new float[]{0.08F, 0.17F, 0.42F};
            case "turquoise" ->
                    new float[]{0.10F, 0.58F, 0.62F};
            case "purple" ->
                    new float[]{0.47F, 0.20F, 0.61F};
            case "silver" ->
                    new float[]{0.60F, 0.64F, 0.70F};
            case "brown" ->
                    new float[]{0.39F, 0.24F, 0.16F};
            default ->
                    new float[]{0.55F, 0.55F, 0.55F};
        };
    }

    private static void draw(
            GuiGraphics gui,
            Minecraft minecraft,
            String text,
            int x,
            int y,
            int color
    ) {
        gui.drawString(
                minecraft.font,
                text,
                x,
                y,
                color,
                true
        );
    }

    private static Entry nearestEntry(
            Minecraft minecraft
    ) {
        if (minecraft.player == null || ENTRIES.isEmpty()) {
            return null;
        }

        Entry nearest = null;
        double nearestSqr = Double.MAX_VALUE;

        for (Entry entry : ENTRIES.values()) {
            if (minecraft.level == null
                    || !minecraft.level.dimension()
                    .equals(entry.dimension)) {
                continue;
            }

            double distanceSqr =
                    minecraft.player.position()
                            .distanceToSqr(entry.position);

            if (distanceSqr < nearestSqr) {
                nearestSqr = distanceSqr;
                nearest = entry;
            }
        }

        return nearest;
    }

    private static String yesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    private static double maxDistanceFor(
            DragonStage stage
    ) {
        return switch (stage) {
            case BABY -> 384.0D;
            case JUVENILE -> 800.0D;
            case ADOLESCENT -> 1000.0D;
            case ADULT -> 1400.0D;
            case ANCIENT -> 1800.0D;
            case COLOSSAL -> 2400.0D;
        };
    }

    private static void trimToLimit() {
        while (ENTRIES.size() > MAX_ENTRIES) {
            Iterator<UUID> iterator =
                    ENTRIES.keySet().iterator();

            if (!iterator.hasNext()) return;

            iterator.next();
            iterator.remove();
        }
    }

    private static final class ScreenPoint {
        private final double x;
        private final double y;
        private final double depth;

        private ScreenPoint(
                double x,
                double y,
                double depth
        ) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
    }

    private static final class Entry {
        private final UUID uuid;
        private int entityId;
        private final ResourceKey<Level> dimension;
        private final DracarysDragonEntity proxy;

        private Vec3 position;
        private float yRot;
        private float xRot;
        private long lastSeenLiveMs;
        private double maxDistance;
        private int ticksSinceFullRefresh;

        private boolean realPresentLastTick = true;
        private long trackingLostMs;
        private long trackingRestoredMs;

        private boolean lastRealPresent;
        private boolean lastForcedRealEligible;
        private boolean lastLodEligible;
        private double lastDistance;

        private long forcedRealAttempts;
        private double lastForcedRealRenderDistance;

        private long lodAttempts;
        private double lastLodRenderDistance;

        private long lastOcclusionCheckTick;
        private boolean lineOfSightClear = true;
        private long impostorFrames;

        private Entry(
                UUID uuid,
                int entityId,
                ResourceKey<Level> dimension,
                DracarysDragonEntity proxy,
                Vec3 position,
                float yRot,
                float xRot,
                long lastSeenLiveMs,
                double maxDistance
        ) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.dimension = dimension;
            this.proxy = proxy;
            this.position = position;
            this.yRot = yRot;
            this.xRot = xRot;
            this.lastSeenLiveMs = lastSeenLiveMs;
            this.maxDistance = maxDistance;
        }
    }
}
