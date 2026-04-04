package com.akitain.enchantmentoverhaul.enchant;

import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class DisabledEnchantments {

    private static final Set<ResourceKey<Enchantment>> DISABLED = Set.of(
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

    public static boolean isDisabled(Holder<Enchantment> entry) {
        return entry.unwrapKey().map(DISABLED::contains).orElse(false);
    }
}
