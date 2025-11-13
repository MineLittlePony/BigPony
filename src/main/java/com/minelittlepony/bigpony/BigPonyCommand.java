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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class BigPonyCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> create() {
        return CommandManager.literal("bigpony")
                .then(config().requires(CommandManager.requirePermissionLevel(3)))
                .then(scale().requires(CommandManager.requirePermissionLevel(2)));
    }

    static LiteralArgumentBuilder<ServerCommandSource> config() {
        var get = CommandManager.literal("get");
        var set = CommandManager.literal("set");
        for (Setting i : Setting.values()) {
            get.then(CommandManager.literal(i.name).executes(i::executeGet));
            set.then(CommandManager.literal(i.name).then(CommandManager.argument("value", i.argumentType.get()).executes(i::executeSet)));
        }

        return CommandManager.literal("config").then(get).then(set);
    }

    static LiteralArgumentBuilder<ServerCommandSource> scale() {

        var get = CommandManager.literal("get");
        var set = CommandManager.literal("set");
        var reset = CommandManager.literal("reset")
                .executes(context -> executeReset(context, context.getSource().getPlayerOrThrow()))
                .then(
                        CommandManager.argument("target", EntityArgumentType.entity())
                        .executes(context -> executeReset(context, EntityArgumentType.getEntity(context, "target")))
                );

        for (var arg : ScaleArg.values()) {
            get.then(CommandManager.literal(arg.name)
                .executes(context -> arg.executeGet(context, context.getSource().getPlayerOrThrow()))
                .then(
                        CommandManager.argument("target", EntityArgumentType.entity())
                            .executes(context -> arg.executeGet(context, EntityArgumentType.getEntity(context, "target")))
                ));
            set.then(
                    CommandManager.literal(arg.name)
                    .then(CommandManager.argument("value", arg.type.argumentType().get())
                            .executes(context -> arg.executeSet(context, context.getSource().getPlayerOrThrow(), context.getArgument("value", Object.class)))
                            .then(
                                    CommandManager.argument("target", EntityArgumentType.entity())
                                        .executes(context -> arg.executeSet(context, EntityArgumentType.getEntity(context, "target"), context.getArgument("value", Object.class)))
                            )
                    )
            );
            reset.then(
                    CommandManager.literal(arg.name)
                        .executes(context -> arg.executeSet(context, context.getSource().getPlayerOrThrow(), 1))
                        .then(
                                CommandManager.argument("target", EntityArgumentType.entity())
                                    .executes(context -> arg.executeSet(context, EntityArgumentType.getEntity(context, "target"), 1))
                        )
            );
        }

        return CommandManager.literal("scale").then(get).then(set).then(reset);
    }

    private static int executeReset(CommandContext<ServerCommandSource> context, Entity target) {
        if (!(target instanceof Scaling.Holder holder)) {
            context.getSource().sendError(Text.translatable("bigpony.command.scale.not_supported"));
            return 0;
        }
        holder.getScaling().setDimensions(EntityScale.DEFAULT);
        if (target == context.getSource().getEntity()) {
            context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.reset.self"), false);
        } else {
            context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.reset", target.getDisplayName()), false);
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

        public int executeGet(CommandContext<ServerCommandSource> context, Entity target) {
            if (!(target instanceof Scaling.Holder holder)) {
                context.getSource().sendError(Text.translatable("bigpony.command.scale.not_supported"));
                return 0;
            }
            Object scale = type.valueGetter.apply(holder.getScaling().getDimensions());
            Text argumentName = Text.translatable("bigpony.argument.scale." + name).formatted(Formatting.GREEN);
            if (target == context.getSource().getEntity()) {
                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.get.self", argumentName, Text.literal(String.valueOf(scale)).formatted(Formatting.GOLD)), false);
            } else {
                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.get", target.getDisplayName(), argumentName, Text.literal(String.valueOf(scale)).formatted(Formatting.GOLD)), false);
            }

            return 0;
        }

        public int executeSet(CommandContext<ServerCommandSource> context, Entity target, Object value) {
            if (value instanceof Float f) {
                if (f != 1 && !BigPony.getInstance().getConfig().allowFreeformResizing.get()) {
                    context.getSource().sendError(Text.translatable("bigpony.command.scale.restricted").formatted(Formatting.RED));
                    return 0;
                }
                if (f < InteractionManager.getInstance().getMinMultiplier() || f > InteractionManager.getInstance().getMaxMultiplier()) {
                    context.getSource().sendError(Text.translatable("bigpony.command.scale.not_permitted",
                            InteractionManager.getInstance().getMinMultiplier(),
                            InteractionManager.getInstance().getMaxMultiplier()).formatted(Formatting.RED)
                    );
                    return 0;
                }
            }
            if (!(target instanceof Scaling.Holder holder)) {
                context.getSource().sendError(Text.translatable("bigpony.command.scale.not_supported"));
                return 0;
            }
            holder.getScaling().setDimensions(type.valueUpdater.apply(holder.getScaling().getDimensions(), cast(value)));
            Text argumentName = Text.translatable("bigpony.argument.scale." + name).formatted(Formatting.GREEN);
            if (target != context.getSource().getEntity()) {
                if (target instanceof ServerPlayerEntity player && context.getSource().getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                    player.sendMessage(Text.translatable("bigpony.scaling.changed"));
                }
                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.set", target.getDisplayName(), argumentName, Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)), false);
            } else {
                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.scale.set.self", argumentName, Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)), false);
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

        public int executeGet(CommandContext<ServerCommandSource> context) {
            context.getSource().sendFeedback(() -> {
                return BigPony.getInstance().getConfig().getCategory("server").getOrEmpty(settingName.toLowerCase(Locale.ROOT)).map(value -> {
                    return Text.translatable("bigpony.command.config.get", Text.literal(name).formatted(Formatting.GREEN), Text.literal(String.valueOf(value.get())).formatted(Formatting.GOLD));
                }).orElseGet(() -> Text.translatable("bigpony.command.config.get.unknown"));
            }, false);
            return 0;
        }

        public int executeSet(CommandContext<ServerCommandSource> context) {
            var config = BigPony.getInstance().getConfig();
            config.getCategory("server").getOrEmpty(settingName.toLowerCase(Locale.ROOT)).ifPresentOrElse(value -> {
                Object newValue = context.getArgument("value", value.get().getClass());
                value.set(newValue);
                config.save();
                InteractionManager.getInstance().sendConfigurationChange(new ConsentPacket());

                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.config.set.success", Text.literal(name).formatted(Formatting.GREEN), Text.literal(String.valueOf(newValue)).formatted(Formatting.GOLD)), false);
            }, () -> {
                context.getSource().sendFeedback(() -> Text.translatable("bigpony.command.config.set.error", Text.literal(name).formatted(Formatting.GREEN)).formatted(Formatting.RED), false);
            });
            return 0;
        }
    }
}
