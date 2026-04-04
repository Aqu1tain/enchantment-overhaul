package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import com.akitain.enchantmentoverhaul.enchant.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static final Enchantment STEP_UP = register("step_up", new StepUpEnchantment());
    public static final Enchantment VENOM = register("venom", new VenomEnchantment());
    public static final Enchantment LAST_STAND = register("last_stand", new LastStandEnchantment());
    public static final Enchantment CURSE_OF_FRAGILITY = register("curse_of_fragility", new CurseOfFragilityEnchantment());
    public static final Enchantment CURSE_OF_HUNGER = register("curse_of_hunger", new CurseOfHungerEnchantment());
    public static final Enchantment VEIL = register("veil", new VeilEnchantment());

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(EnchantmentOverhaul.MOD_ID, name), enchantment);
    }

    public static void register() {}
}
