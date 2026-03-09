package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class SellEnchantedToolMixin {

    @Inject(method = "create", at = @At("RETURN"))
    private void enforceSlotLimit(ServerWorld world, Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        TradeOffer offer = cir.getReturnValue();
        if (offer == null) return;

        ItemStack stack = offer.getSellItem();
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        if (enchantments.isEmpty()) return;

        int maxSlots = SlotSystem.getBaseMaxSlots(stack);
        int usedSlots = 0;
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
            if (DisabledEnchantments.isDisabled(entry.getKey())) continue;

            int level = entry.getIntValue();
            int cost = entry.getKey().isIn(EnchantmentTags.CURSE) ? 0
                    : entry.getKey().matchesKey(Enchantments.MENDING) ? 3
                    : level;

            if (usedSlots + cost <= maxSlots) {
                builder.add(entry.getKey(), level);
                usedSlots += cost;
            }
        }

        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
    }
}
