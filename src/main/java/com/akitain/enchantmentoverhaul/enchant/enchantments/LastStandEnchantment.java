package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class LastStandEnchantment extends Enchantment {

    public LastStandEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMinPower(int level) { return 7 + (level - 1) * 10; }

    @Override
    public int getMaxPower(int level) { return getMinPower(level) + 30; }

    @Override
    public int getMaxLevel() { return 3; }
}
