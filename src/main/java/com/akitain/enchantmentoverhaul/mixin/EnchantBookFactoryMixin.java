package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.entity.Entity;
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

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private void disableEnchantedBookTrades(ServerWorld world, Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        cir.setReturnValue(null);
    }
}
