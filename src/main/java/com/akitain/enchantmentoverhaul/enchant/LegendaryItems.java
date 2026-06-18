package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class LegendaryItems {

    public static final Formatting COLOR = Formatting.GOLD;

    private LegendaryItems() {}

    public static boolean isLegendary(ItemStack stack) {
        return EnchantmentHelper.getLevel(ModEnchantments.VENOM, stack) > 0
                && EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack) > 0;
    }

    public static Text tooltip() {
        return Text.translatable("item.enchantment-overhaul.legendary.tooltip")
                .setStyle(Style.EMPTY.withColor(COLOR));
    }
}
