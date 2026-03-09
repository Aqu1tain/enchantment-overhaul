package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModAdvancements;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ArmorEquipMixin {

    @Inject(method = "onEquipStack", at = @At("TAIL"))
    private void onArmorChanged(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (!slot.isArmorSlot()) return;
        if ((Object) this instanceof ServerPlayerEntity player) {
            ModAdvancements.checkWalkingFortress(player);
        }
    }
}
