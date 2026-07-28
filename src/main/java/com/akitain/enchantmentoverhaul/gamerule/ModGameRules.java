package com.akitain.enchantmentoverhaul.gamerule;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class ModGameRules {

    public static GameRule<Boolean> MOB_GEAR_ENCHANTMENTS;
    public static GameRule<Boolean> HONING_ON_AXES;
    public static GameRule<Boolean> ENCHANTING_XP_COST;

    private ModGameRules() {}

    public static void register() {
        MOB_GEAR_ENCHANTMENTS = GameRuleBuilder.forBoolean(true)
                .category(GameRuleCategory.MOBS)
                .buildAndRegister(Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, "mob_gear_enchantments"));

        HONING_ON_AXES = GameRuleBuilder.forBoolean(true)
                .category(GameRuleCategory.PLAYER)
                .buildAndRegister(Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, "honing_on_axes"));

        ENCHANTING_XP_COST = GameRuleBuilder.forBoolean(true)
                .category(GameRuleCategory.PLAYER)
                .buildAndRegister(Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, "enchanting_xp_cost"));
    }
}
