package com.music4music.enchantmentoverhaul.enchant;

import com.music4music.enchantmentoverhaul.component.ModComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;
import java.util.Set;

public class SlotSystem {

    public static int getBaseMaxSlots(ItemStack stack) {
        Item item = stack.getItem();
        String id = Registries.ITEM.getId(item).getPath();

        if (id.startsWith("netherite_")) return 5;
        if (id.startsWith("diamond_")) return 5;
        if (id.startsWith("golden_")) return 6;
        if (id.startsWith("iron_") || id.startsWith("chainmail_")) return 4;
        if (id.startsWith("copper_")) return 3;
        if (id.startsWith("leather_") || id.startsWith("wooden_") || id.startsWith("stone_")) return 3;

        if (item == Items.TRIDENT || item == Items.MACE || item == Items.ELYTRA) return 5;
        if (item == Items.TURTLE_HELMET) return 5;
        if (item == Items.CROSSBOW) return 4;
        if (item == Items.BOW || item == Items.FISHING_ROD) return 3;

        if (stack.isEnchantable() || hasEnchantments(stack)) return 3;

        return 0;
    }

    public static int getGrindstonePenalty(ItemStack stack) {
        return stack.getOrDefault(ModComponents.GRINDSTONE_PENALTY, 0);
    }

    public static int getCurseBonus(ItemStack stack) {
        int bonus = 0;
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : getEnchantments(stack).getEnchantmentEntries()) {
            if (entry.getKey().isIn(EnchantmentTags.CURSE)) bonus++;
        }
        return bonus;
    }

    public static int getUsedSlots(ItemStack stack) {
        int used = 0;
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : getEnchantments(stack).getEnchantmentEntries()) {
            RegistryEntry<Enchantment> enchantment = entry.getKey();
            int level = entry.getIntValue();

            if (enchantment.isIn(EnchantmentTags.CURSE)) continue;
            if (enchantment.matchesKey(Enchantments.MENDING)) { used += 3; continue; }

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

    public static boolean canApplyEnchantment(ItemStack stack, RegistryEntry<Enchantment> enchantment, int level) {
        int cost = enchantment.matchesKey(Enchantments.MENDING) ? 3 : level;
        if (enchantment.isIn(EnchantmentTags.CURSE)) cost = 0;
        return cost <= getAvailableSlots(stack);
    }

    private static ItemEnchantmentsComponent getEnchantments(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
    }

    private static boolean hasEnchantments(ItemStack stack) {
        return !getEnchantments(stack).isEmpty();
    }
}
