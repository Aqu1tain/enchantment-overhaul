package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class LegendaryItems {

    public static final Formatting COLOR = Formatting.GOLD;

    private LegendaryItems() {}

    public static boolean isLegendary(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        boolean venom = false;
        boolean fireAspect = false;
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.VENOM)) venom = true;
            if (entry.getKey().matchesKey(Enchantments.FIRE_ASPECT)) fireAspect = true;
            if (venom && fireAspect) return true;
        }
        return false;
    }

    public static Text tooltip() {
        return Text.translatable("item.enchantment-overhaul.legendary.tooltip")
                .formatted(Formatting.GRAY, Formatting.ITALIC);
    }
}
