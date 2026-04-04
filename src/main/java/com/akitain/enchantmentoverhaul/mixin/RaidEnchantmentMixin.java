package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public class RaidEnchantmentMixin {

    @Inject(method = "getEnchantOdds", at = @At("HEAD"), cancellable = true)
    private void disableRaidEnchantments(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0f);
    }
}
