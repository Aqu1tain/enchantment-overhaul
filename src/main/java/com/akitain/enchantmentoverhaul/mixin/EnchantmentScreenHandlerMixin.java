package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void disableEnchanting(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"), cancellable = true)
    private void disableEnchantmentGeneration(Inventory inventory, CallbackInfo ci) {
        ci.cancel();
    }
}
