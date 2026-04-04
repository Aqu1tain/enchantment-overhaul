package com.akitain.enchantmentoverhaul.loot;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootEnchanter {

    public static void tryEnchant(ItemStack stack, LootContext context) {
        if (!stack.isDamageable()) return;
        if (!EnchantmentHelper.get(stack).isEmpty()) return;

        int maxSlots = SlotSystem.getBaseMaxSlots(stack);
        if (maxSlots <= 0) return;

        Random random = context.getRandom();
        float chance = maxSlots * 0.03f;
        if (random.nextFloat() >= chance) return;

        List<Enchantment> valid = new ArrayList<>();
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (DisabledEnchantments.isDisabled(enchantment)) continue;
            if (enchantment.isCursed()) continue;
            if (enchantment == Enchantments.MENDING) continue;
            if (!enchantment.isAcceptableItem(stack)) continue;
            valid.add(enchantment);
        }

        if (valid.isEmpty()) return;

        Enchantment chosen = valid.get(random.nextInt(valid.size()));
        stack.addEnchantment(chosen, 1);
    }
}
