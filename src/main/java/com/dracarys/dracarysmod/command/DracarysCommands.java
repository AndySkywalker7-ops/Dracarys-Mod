package com.dracarys.dracarysmod.command;

import com.dracarys.dracarysmod.dragon.DragonSizeTier;
import com.dracarys.dracarysmod.dragon.DragonStage;
import com.dracarys.dracarysmod.dragon.DragonVariant;
import com.dracarys.dracarysmod.entity.DracarysDragonEntity;
import com.dracarys.dracarysmod.registry.ModEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class DracarysCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dracarys")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("spawn")
                .then(Commands.argument("variant", StringArgumentType.word())
                    .then(Commands.argument("size", StringArgumentType.word())
                        .then(Commands.argument("stage", StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                DracarysDragonEntity dragon = ModEntities.DRAGON.get().create(player.serverLevel());
                                if (dragon == null) return 0;

                                DragonVariant variant = DragonVariant.byName(StringArgumentType.getString(context, "variant"));
                                DragonSizeTier size = DragonSizeTier.byName(StringArgumentType.getString(context, "size"));
                                DragonStage stage;
                                try {
                                    stage = DragonStage.valueOf(StringArgumentType.getString(context, "stage").toUpperCase());
                                } catch (IllegalArgumentException ignored) {
                                    stage = DragonStage.ADULT;
                                }

                                dragon.moveTo(player.getX() + 2, player.getY() + 1, player.getZ() + 2, player.getYRot(), 0.0F);
                                dragon.configureForCommand(variant, size, stage);
                                player.serverLevel().addFreshEntity(dragon);
                                context.getSource().sendSuccess(
                                    () -> Component.literal("Spawned " + variant.id() + " " + size.name().toLowerCase() + " " + stage.name().toLowerCase() + " dragon"),
                                    true
                                );
                                return 1;
                            })))))
            .then(Commands.literal("info")
                .executes(context -> {
                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;
                    var dragons = player.serverLevel().getEntitiesOfClass(
                        DracarysDragonEntity.class,
                        player.getBoundingBox().inflate(20)
                    );
                    if (dragons.isEmpty()) {
                        context.getSource().sendSuccess(() -> Component.literal("No dragon within 20 blocks."), false);
                        return 0;
                    }
                    var dragon = dragons.get(0);
                    String description = "Dragon: " + dragon.getVariant().id()
                        + ", " + dragon.getSizeTier().name().toLowerCase()
                        + ", " + dragon.getStage().name().toLowerCase()
                        + ", conceptual length " + String.format("%.1f", dragon.conceptualLength()) + " blocks";
                    context.getSource().sendSuccess(() -> Component.literal(description), false);
                    return 1;
                }))
        );
    }

    private DracarysCommands() {}
}
