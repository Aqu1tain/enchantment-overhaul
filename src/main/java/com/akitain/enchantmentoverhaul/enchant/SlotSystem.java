package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Map;

public class SlotSystem {

    public static int getBaseMaxSlots(ItemStack stack) {
        if (!stack.isDamageable()) return 0;

        Item item = stack.getItem();
        String id = Registries.ITEM.getId(item).getPath();

        if (id.startsWith("netherite_")) return 5;
        if (id.startsWith("diamond_")) return 5;
        if (id.startsWith("golden_")) return 6;
        if (id.startsWith("iron_") || id.startsWith("chainmail_")) return 4;
        if (id.startsWith("copper_")) return 3;
        if (id.startsWith("leather_") || id.startsWith("wooden_") || id.startsWith("stone_")) return 3;

        if (item == Items.TRIDENT || item == Items.ELYTRA) return 5;
        if (item == Items.TURTLE_HELMET) return 5;
        if (item == Items.CROSSBOW) return 4;
        if (item == Items.BOW || item == Items.FISHING_ROD) return 3;

        if (stack.isEnchantable() || !EnchantmentHelper.get(stack).isEmpty()) return 3;

        return 0;
    }

    public static int getGrindstonePenalty(ItemStack stack) {
        return ModComponents.getInt(stack, ModComponents.GRINDSTONE_PENALTY, 0);
    }

    public static int getCurseBonus(ItemStack stack) {
        int bonus = 0;
        for (Enchantment enchantment : EnchantmentHelper.get(stack).keySet()) {
            if (enchantment.isCursed()) bonus++;
        }
        return bonus;
    }

    public static int getUsedSlots(ItemStack stack) {
        int used = 0;
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.get(stack).entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (enchantment.isCursed()) continue;
            if (enchantment == Enchantments.MENDING) { used += 3; continue; }

            used += level;
        }
        return used;
    }

    public static int getMaxSlots(ItemStack stack) {
        return getBaseMaxSlots(stack) - getGrindstonePenalty(stack) + getCurseBonus(stack);
    }

    public static int getAvailableSlots(ItemStack stack) {
        return getMaxSlots(stack) - getUsedSlots(stack);
    }

    public static boolean canApplyEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        int cost = enchantment == Enchantments.MENDING ? 3 : level;
        if (enchantment.isCursed()) cost = 0;
        return cost <= getAvailableSlots(stack);
    }
}
