package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class BurnishingEnchantment extends Enchantment {

    public BurnishingEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    @Override
    public int getMinPower(int level) { return 15; }

    @Override
    public int getMaxPower(int level) { return 50; }

    @Override
    public int getMaxLevel() { return 1; }
}
