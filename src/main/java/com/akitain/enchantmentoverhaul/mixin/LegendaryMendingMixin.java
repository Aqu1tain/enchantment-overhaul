package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class LegendaryMendingMixin {

    @Inject(method = "getRepairWithExperience", at = @At("HEAD"), cancellable = true)
    private static void blockLegendaryMending(ServerWorld world, ItemStack stack, int xp, CallbackInfoReturnable<Integer> cir) {
        if (LegendaryItems.isLegendary(stack)) cir.setReturnValue(0);
    }
}
