package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class WraithEnchantment extends Enchantment {

    public WraithEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) { return 15; }

    @Override
    public int getMaxPower(int level) { return 30; }

    @Override
    public int getMaxLevel() { return 1; }
}
