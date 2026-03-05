package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Set;

public class DisabledEnchantments {

    private static final Set<RegistryKey<Enchantment>> DISABLED = Set.of(
            Enchantments.PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.BLAST_PROTECTION,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.POWER,
            Enchantments.DENSITY,
            Enchantments.IMPALING,
            Enchantments.UNBREAKING,
            Enchantments.EFFICIENCY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.PUNCH
    );

    public static boolean isDisabled(RegistryEntry<Enchantment> entry) {
        return entry.getKey().map(DISABLED::contains).orElse(false);
    }
}
