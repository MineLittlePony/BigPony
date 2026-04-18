package com.minelittlepony.bigpony;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.CameraScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.network.ConsentPacket;
import com.minelittlepony.bigpony.network.InteractionManager;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRules;

public class BigPonyCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("bigpony")
                .then(config().requires(Commands.hasPermission(Commands.LEVEL_ADMINS)))
                .then(scale().requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)));
    }

    static LiteralArgumentBuilder<CommandSourceStack> config() {
        var get = Commands.literal("get");
        var set = Commands.literal("set");
        for (Setting i : Setting.values()) {
            get.then(Commands.literal(i.name).executes(i::executeGet));
            set.then(Commands.literal(i.name).then(Commands.argument("value", i.argumentType.get()).executes(i::executeSet)));
        }

        return Commands.literal("config").then(get).then(set);
    }

    static LiteralArgumentBuilder<CommandSourceStack> scale() {

        var get = Commands.literal("get");
        var set = Commands.literal("set");
        var reset = Commands.literal("reset")
                .executes(context -> executeReset(context, context.getSource().getPlayerOrException()))
                .then(
                        Commands.argument("target", EntityArgument.entity())
                        .executes(context -> executeReset(context, EntityArgument.getEntity(context, "target")))
                );

        for (var arg : ScaleArg.values()) {
            get.then(Commands.literal(arg.name)
                .executes(context -> arg.executeGet(context, context.getSource().getPlayerOrException()))
                .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes(context -> arg.executeGet(context, EntityArgument.getEntity(context, "target")))
                ));
            set.then(
                    Commands.literal(arg.name)
                    .then(Commands.argument("value", arg.type.argumentType().get())
                            .executes(context -> arg.executeSet(context, context.getSource().getPlayerOrException(), context.getArgument("value", Object.class)))
                            .then(
                                    Commands.argument("target", EntityArgument.entity())
                                        .executes(context -> arg.executeSet(context, EntityArgument.getEntity(context, "target"), context.getArgument("value", Object.class)))
                            )
                    )
            );
            reset.then(
                    Commands.literal(arg.name)
                        .executes(context -> arg.executeSet(context, context.getSource().getPlayerOrException(), 1))
                        .then(
                                Commands.argument("target", EntityArgument.entity())
                                    .executes(context -> arg.executeSet(context, EntityArgument.getEntity(context, "target"), 1))
                        )
            );
        }

        return Commands.literal("scale").then(get).then(set).then(reset);
    }

    private static int executeReset(CommandContext<CommandSourceStack> context, Entity target) {
        if (!(target instanceof Scaling.Holder holder)) {
            context.getSource().sendFailure(Component.translatable("bigpony.command.scale.not_supported"));
            return 0;
        }
        holder.getScaling().setDimensions(EntityScale.DEFAULT);
        if (target == context.getSource().getEntity()) {
            context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.reset.self"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.reset", target.getDisplayName()), false);
        }
        return 0;
    }

    record ScalingArgType<T>(Function<EntityScale, T> valueGetter, BiFunction<EntityScale, T, EntityScale> valueUpdater, Supplier<ArgumentType<T>> argumentType) {}

    public enum ScaleArg {
        SCALE(scale -> scale.body().shadowScale(), (scale, value) -> scale.withModel(BodyScale.of(value)).withCamera(CameraScale.of(value))),
        MODEL(scale -> scale.body().shadowScale(), (scale, value) -> scale.withModel(BodyScale.of(value))),
        MODEL_X(scale -> scale.body().x(), (scale, value) -> scale.withModel(scale.model().withX(value))),
        MODEL_Y(scale -> scale.body().y(), (scale, value) -> scale.withModel(scale.model().withY(value))),
        MODEL_Z(scale -> scale.body().z(), (scale, value) -> scale.withModel(scale.model().withZ(value))),
        HITBOX(scale -> scale.body().shadowScale(), (scale, value) -> scale.withHitbox(BodyScale.of(value))),
        HITBOX_X(scale -> scale.body().x(), (scale, value) -> scale.withHitbox(scale.body().withX(value))),
        HITBOX_Y(scale -> scale.body().y(), (scale, value) -> scale.withHitbox(scale.body().withY(value))),
        HITBOX_Z(scale -> scale.body().z(), (scale, value) -> scale.withHitbox(scale.body().withZ(value))),
        CAMERA(scale -> scale.camera().height(), (scale, value) -> scale.withCamera(CameraScale.of(value))),
        CAMERA_HEIGHT(scale -> scale.camera().height(), (scale, value) -> scale.withCamera(scale.camera().withHeight(value))),
        CAMERA_DISTANCE(scale -> scale.camera().distance(), (scale, value) -> scale.withCamera(scale.camera().withDistance(value)));

        private final String name = name().toLowerCase(Locale.ROOT);
        private final ScalingArgType<?> type;

        ScaleArg(Function<EntityScale, Float> valueGetter, BiFunction<EntityScale, Float, EntityScale> valueUpdater) {
            this(new ScalingArgType<>(valueGetter, valueUpdater, () -> FloatArgumentType.floatArg(0)));
        }

        ScaleArg(ScalingArgType<?> type) {
            this.type = type;
        }

        public int executeGet(CommandContext<CommandSourceStack> context, Entity target) {
            if (!(target instanceof Scaling.Holder holder)) {
                context.getSource().sendFailure(Component.translatable("bigpony.command.scale.not_supported"));
                return 0;
            }
            Object scale = type.valueGetter.apply(holder.getScaling().getDimensions());
            Component argumentName = Component.translatable("bigpony.argument.scale." + name).withStyle(ChatFormatting.GREEN);
            if (target == context.getSource().getEntity()) {
                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.get.self", argumentName, Component.literal(String.valueOf(scale)).withStyle(ChatFormatting.GOLD)), false);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.get", target.getDisplayName(), argumentName, Component.literal(String.valueOf(scale)).withStyle(ChatFormatting.GOLD)), false);
            }

            return 0;
        }

        public int executeSet(CommandContext<CommandSourceStack> context, Entity target, Object value) {
            if (value instanceof Float f) {
                if (f != 1 && !BigPony.getInstance().getConfig().allowFreeformResizing.get()) {
                    context.getSource().sendFailure(Component.translatable("bigpony.command.scale.restricted").withStyle(ChatFormatting.RED));
                    return 0;
                }
                if (f < InteractionManager.getInstance().getMinMultiplier() || f > InteractionManager.getInstance().getMaxMultiplier()) {
                    context.getSource().sendFailure(Component.translatable("bigpony.command.scale.not_permitted",
                            InteractionManager.getInstance().getMinMultiplier(),
                            InteractionManager.getInstance().getMaxMultiplier()).withStyle(ChatFormatting.RED)
                    );
                    return 0;
                }
            }
            if (!(target instanceof Scaling.Holder holder)) {
                context.getSource().sendFailure(Component.translatable("bigpony.command.scale.not_supported"));
                return 0;
            }
            holder.getScaling().setDimensions(type.valueUpdater.apply(holder.getScaling().getDimensions(), cast(value)));
            Component argumentName = Component.translatable("bigpony.argument.scale." + name).withStyle(ChatFormatting.GREEN);
            if (target != context.getSource().getEntity()) {
                if (target instanceof ServerPlayer player && context.getSource().getLevel().getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)) {
                    player.sendSystemMessage(Component.translatable("bigpony.scaling.changed"));
                }
                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.set", target.getDisplayName(), argumentName, Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), false);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.scale.set.self", argumentName, Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), false);
            }
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T cast(Object o) {
        return (T)o;
    }

    public enum Setting {
        MIN_SCALE("minScalingMultiplier", () -> FloatArgumentType.floatArg(0)),
        MAX_SCALE("maxScalingMultiplier", () -> FloatArgumentType.floatArg(0)),
        ALLOW_HITBOX_CHANGES("allowHitboxChanges", BoolArgumentType::bool),
        ALLOW_CAMERA_CHANGES("allowCameraChanges", BoolArgumentType::bool),
        ALLOW_FREEFORM_RESIZING("allowFreeformResizing", BoolArgumentType::bool),
        LOG_NETWORK_EVENTS("logNetworkEvents", BoolArgumentType::bool);

        private final String name = name().toLowerCase(Locale.ROOT);
        private final String settingName;
        private final Supplier<ArgumentType<?>> argumentType;

        Setting(String settingName, Supplier<ArgumentType<?>> argumentType) {
            this.settingName = settingName;
            this.argumentType = argumentType;
        }

        public int executeGet(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSuccess(() -> {
                return BigPony.getInstance().getConfig().getCategory("server").getOrEmpty(settingName.toLowerCase(Locale.ROOT)).map(value -> {
                    return Component.translatable("bigpony.command.config.get", Component.literal(name).withStyle(ChatFormatting.GREEN), Component.literal(String.valueOf(value.get())).withStyle(ChatFormatting.GOLD));
                }).orElseGet(() -> Component.translatable("bigpony.command.config.get.unknown"));
            }, false);
            return 0;
        }

        public int executeSet(CommandContext<CommandSourceStack> context) {
            var config = BigPony.getInstance().getConfig();
            config.getCategory("server").getOrEmpty(settingName.toLowerCase(Locale.ROOT)).ifPresentOrElse(value -> {
                Object newValue = context.getArgument("value", value.get().getClass());
                value.set(newValue);
                config.save();
                InteractionManager.getInstance().sendConfigurationChange(new ConsentPacket());

                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.config.set.success", Component.literal(name).withStyle(ChatFormatting.GREEN), Component.literal(String.valueOf(newValue)).withStyle(ChatFormatting.GOLD)), false);
            }, () -> {
                context.getSource().sendSuccess(() -> Component.translatable("bigpony.command.config.set.error", Component.literal(name).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED), false);
            });
            return 0;
        }
    }
}
