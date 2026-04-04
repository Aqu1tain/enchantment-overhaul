package com.akitain.enchantmentoverhaul.loot;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootEnchanter {

    public static void tryEnchant(ItemStack stack, LootContext context) {
        if (!stack.isDamageableItem()) return;
        if (!stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) return;

        int maxSlots = SlotSystem.getBaseMaxSlots(stack);
        if (maxSlots <= 0) return;

        RandomSource random = context.getRandom();
        float chance = maxSlots * 0.03f;
        if (random.nextFloat() >= chance) return;

        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> valid = new ArrayList<>();
        for (Holder<Enchantment> entry : registry.asHolderIdMap()) {
            if (entry.unwrapKey().isEmpty()) continue;
            if (DisabledEnchantments.isDisabled(entry)) continue;
            if (entry.is(EnchantmentTags.CURSE)) continue;
            if (entry.is(Enchantments.MENDING)) continue;
            if (!entry.value().canEnchant(stack)) continue;
            valid.add(entry);
        }

        if (valid.isEmpty()) return;

        Holder<Enchantment> chosen = valid.get(random.nextInt(valid.size()));
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        builder.upgrade(chosen, 1);
        stack.set(DataComponents.ENCHANTMENTS, builder.toImmutable());
    }
}
