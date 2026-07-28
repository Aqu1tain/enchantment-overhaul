package com.akitain.enchantmentoverhaul.command;

import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class UpgradeCommand {

    private static final int MAX_LEVEL = 5;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("upgrade")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(literal("honing").executes(context -> apply(context.getSource(), UpgradeType.HONING, MAX_LEVEL))
                                .then(argument("level", IntegerArgumentType.integer(1, MAX_LEVEL))
                                        .executes(context -> apply(context.getSource(), UpgradeType.HONING, IntegerArgumentType.getInteger(context, "level")))))
                        .then(literal("warding").executes(context -> apply(context.getSource(), UpgradeType.WARDING, MAX_LEVEL))
                                .then(argument("level", IntegerArgumentType.integer(1, MAX_LEVEL))
                                        .executes(context -> apply(context.getSource(), UpgradeType.WARDING, IntegerArgumentType.getInteger(context, "level")))))
                        .then(literal("tempering").executes(context -> apply(context.getSource(), UpgradeType.TEMPERING, MAX_LEVEL))
                                .then(argument("level", IntegerArgumentType.integer(1, MAX_LEVEL))
                                        .executes(context -> apply(context.getSource(), UpgradeType.TEMPERING, IntegerArgumentType.getInteger(context, "level")))))
                        .then(literal("grinding").executes(context -> apply(context.getSource(), UpgradeType.GRINDING, MAX_LEVEL))
                                .then(argument("level", IntegerArgumentType.integer(1, MAX_LEVEL))
                                        .executes(context -> apply(context.getSource(), UpgradeType.GRINDING, IntegerArgumentType.getInteger(context, "level")))))));
    }

    private static int apply(CommandSourceStack source, UpgradeType type, int level) {
        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        ItemStack stack = source.getPlayer().getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.literal("Hold an item before using /upgrade."));
            return 0;
        }

        if (!type.appliesTo(stack, source.getLevel())) {
            source.sendFailure(Component.literal(typeName(type) + " cannot be applied to this item."));
            return 0;
        }

        type.applyTo(stack, level);
        source.sendSuccess(() -> Component.literal("Applied " + typeName(type) + " " + roman(level) + " to held item."), true);
        return 1;
    }

    private static String typeName(UpgradeType type) {
        String lower = type.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}
