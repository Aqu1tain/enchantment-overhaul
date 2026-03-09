package com.akitain.enchantmentoverhaul.loot;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class LootEnchanter {

    public static void tryEnchant(ItemStack stack, LootContext context) {
        if (!stack.isDamageable()) return;
        if (!stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty()) return;

        int maxSlots = SlotSystem.getBaseMaxSlots(stack);
        if (maxSlots <= 0) return;

        Random random = context.getRandom();
        float chance = maxSlots * 0.03f;
        if (random.nextFloat() >= chance) return;

        var registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        List<RegistryEntry<Enchantment>> valid = new ArrayList<>();
        for (RegistryEntry<Enchantment> entry : registry.getIndexedEntries()) {
            if (entry.getKey().isEmpty()) continue;
            if (DisabledEnchantments.isDisabled(entry)) continue;
            if (entry.isIn(EnchantmentTags.CURSE)) continue;
            if (entry.matchesKey(Enchantments.MENDING)) continue;
            if (!entry.value().isAcceptableItem(stack)) continue;
            valid.add(entry);
        }

        if (valid.isEmpty()) return;

        RegistryEntry<Enchantment> chosen = valid.get(random.nextInt(valid.size()));
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.add(chosen, 1);
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
    }
}
