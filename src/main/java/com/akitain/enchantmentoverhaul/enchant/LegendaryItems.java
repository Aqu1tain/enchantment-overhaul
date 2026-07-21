package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class LegendaryItems {

    public static final ChatFormatting COLOR = ChatFormatting.GOLD;

    private LegendaryItems() {}

    public static boolean isLegendary(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) return false;

        boolean venom = false;
        boolean fireAspect = false;
        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(ModEnchantments.VENOM)) venom = true;
            if (entry.getKey().is(Enchantments.FIRE_ASPECT)) fireAspect = true;
            if (venom && fireAspect) return true;
        }
        return false;
    }

    public static Component tooltip() {
        return Component.translatable("item.enchantment-overhaul.legendary.tooltip")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    }
}
