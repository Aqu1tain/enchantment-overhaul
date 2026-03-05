package com.music4music.enchantmentoverhaul.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantWithLevelsLootFunction.class)
public class EnchantWithLevelsLootFunctionMixin {

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void disableLootEnchanting(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(stack);
    }
}
