package com.akitain.enchantmentoverhaul.enchant.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;

public class VenomEnchantment extends Enchantment {

    public VenomEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) { return 7 + (level - 1) * 20; }

    @Override
    public int getMaxPower(int level) { return getMinPower(level) + 30; }

    @Override
    public int getMaxLevel() { return 2; }

    @Override
    public boolean canAccept(Enchantment other) {
        return other != Enchantments.FIRE_ASPECT && super.canAccept(other);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof TridentItem;
    }
}
