package com.music4music.enchantmentoverhaul.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookFactoryMixin {

    @Inject(method = "create", at = @At("RETURN"), cancellable = true)
    private void makeBooksLevelless(ServerWorld world, Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        TradeOffer offer = cir.getReturnValue();
        if (offer == null) return;

        ItemStack book = offer.getSellItem();
        ItemEnchantmentsComponent enchantments = book.getOrDefault(
                DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(Enchantments.MENDING)
                    || entry.matchesKey(Enchantments.FROST_WALKER)
                    || entry.matchesKey(Enchantments.BANE_OF_ARTHROPODS)) {
                cir.setReturnValue(null);
                return;
            }
        }

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            builder.set(entry, 1);
        }
        book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
    }
}
