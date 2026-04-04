package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class StepUpEnchantment extends Enchantment {

    public StepUpEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) { return 10; }

    @Override
    public int getMaxPower(int level) { return 25; }

    @Override
    public int getMaxLevel() { return 1; }
}
