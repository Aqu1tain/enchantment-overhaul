package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class CurseOfHungerEnchantment extends Enchantment {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public CurseOfHungerEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMinPower(int level) { return 25; }

    @Override
    public int getMaxPower(int level) { return 50; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public boolean isCursed() { return true; }

    @Override
    public boolean isTreasure() { return true; }
}
