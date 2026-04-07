package com.akitain.enchantmentoverhaul.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public final class ModGameRules {

    public static GameRules.Key<GameRules.BooleanRule> MOB_GEAR_ENCHANTMENTS;

    private ModGameRules() {}

    public static void register() {
        MOB_GEAR_ENCHANTMENTS = GameRuleRegistry.register(
                "mobGearEnchantments",
                GameRules.Category.MOBS,
                GameRuleFactory.createBooleanRule(true)
        );
    }
}
