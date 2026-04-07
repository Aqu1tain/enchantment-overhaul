package com.akitain.enchantmentoverhaul.gamerule;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class ModGameRules {

    public static GameRule<Boolean> MOB_GEAR_ENCHANTMENTS;

    private ModGameRules() {}

    public static void register() {
        MOB_GEAR_ENCHANTMENTS = GameRuleBuilder.forBoolean(true)
                .category(GameRuleCategory.MOBS)
                .buildAndRegister(Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, "mob_gear_enchantments"));
    }
}
