package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class CurseOfFragilityEnchantment extends Enchantment {

    public CurseOfFragilityEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]{});
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
