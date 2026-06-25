package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> STEP_UP = of("step_up");
    public static final ResourceKey<Enchantment> VENOM = of("venom");
    public static final ResourceKey<Enchantment> LAST_STAND = of("last_stand");
    public static final ResourceKey<Enchantment> CURSE_OF_FRAGILITY = of("curse_of_fragility");
    public static final ResourceKey<Enchantment> CURSE_OF_HUNGER = of("curse_of_hunger");
    public static final ResourceKey<Enchantment> VEIL = of("veil");
    public static final ResourceKey<Enchantment> BURNISHING = of("burnishing");
    public static final ResourceKey<Enchantment> WRAITH = of("wraith");

    private static ResourceKey<Enchantment> of(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, name));
    }
}
