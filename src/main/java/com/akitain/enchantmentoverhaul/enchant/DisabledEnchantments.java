package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;

import java.util.Set;

public class DisabledEnchantments {

    private static final Set<Enchantment> DISABLED = Set.of(
            Enchantments.PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.BLAST_PROTECTION,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.POWER,
            Enchantments.IMPALING,
            Enchantments.UNBREAKING,
            Enchantments.EFFICIENCY,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.PUNCH
    );

    public static boolean isDisabled(Enchantment enchantment) {
        return DISABLED.contains(enchantment);
    }
}
