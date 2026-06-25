package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static final RegistryKey<Enchantment> STEP_UP = of("step_up");
    public static final RegistryKey<Enchantment> VENOM = of("venom");
    public static final RegistryKey<Enchantment> LAST_STAND = of("last_stand");
    public static final RegistryKey<Enchantment> CURSE_OF_FRAGILITY = of("curse_of_fragility");
    public static final RegistryKey<Enchantment> CURSE_OF_HUNGER = of("curse_of_hunger");
    public static final RegistryKey<Enchantment> VEIL = of("veil");
    public static final RegistryKey<Enchantment> BURNISHING = of("burnishing");
    public static final RegistryKey<Enchantment> WRAITH = of("wraith");

    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(EnchantmentOverhaul.MOD_ID, name));
    }
}
