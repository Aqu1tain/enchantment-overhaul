package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class SlotSystem {

    public static int getBaseMaxSlots(ItemStack stack) {
        if (!stack.isDamageableItem()) return 0;

        Item item = stack.getItem();
        String id = BuiltInRegistries.ITEM.getKey(item).getPath();

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
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : getEnchantments(stack).entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) bonus++;
        }
        return bonus;
    }

    public static int getUsedSlots(ItemStack stack) {
        int used = 0;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : getEnchantments(stack).entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            int level = entry.getIntValue();

            if (enchantment.is(EnchantmentTags.CURSE)) continue;
            if (enchantment.is(Enchantments.MENDING)) { used += 3; continue; }

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

    public static boolean canApplyEnchantment(ItemStack stack, Holder<Enchantment> enchantment, int level) {
        int cost = enchantment.is(Enchantments.MENDING) ? 3 : level;
        if (enchantment.is(EnchantmentTags.CURSE)) cost = 0;
        return cost <= getAvailableSlots(stack);
    }

    private static ItemEnchantments getEnchantments(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
    }

    private static boolean hasEnchantments(ItemStack stack) {
        return !getEnchantments(stack).isEmpty();
    }
}
