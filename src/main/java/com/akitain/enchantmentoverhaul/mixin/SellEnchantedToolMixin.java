package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(TradeOffers.EnchantedItemForEmeralds.class)
public class SellEnchantedToolMixin {

    @Inject(method = "create", at = @At("RETURN"))
    private void enforceSlotLimit(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        TradeOffer offer = cir.getReturnValue();
        if (offer == null) return;

        ItemStack stack = offer.getSellItem();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        if (enchantments.isEmpty()) return;

        int maxSlots = SlotSystem.getBaseMaxSlots(stack);
        int usedSlots = 0;
        Map<Enchantment, Integer> filtered = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (DisabledEnchantments.isDisabled(enchantment)) continue;

            int cost = enchantment.isCursed() ? 0
                    : enchantment == Enchantments.MENDING ? 3
                    : level;

            if (usedSlots + cost <= maxSlots) {
                filtered.put(enchantment, level);
                usedSlots += cost;
            }
        }

        EnchantmentHelper.set(filtered, stack);
    }
}
