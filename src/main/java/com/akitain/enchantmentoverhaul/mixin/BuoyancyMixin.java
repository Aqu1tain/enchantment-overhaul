package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class BuoyancyMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void applyBuoyancy(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.isTouchingWater() || self.isSneaking()) return;

        ItemStack boots = self.getEquippedStack(EquipmentSlot.FEET);
        ItemEnchantmentsComponent enchantments = boots.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        boolean hasBuoyancy = false;
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.BUOYANCY)) { hasBuoyancy = true; break; }
        }
        if (!hasBuoyancy) return;

        if (self.isSubmergedInWater()) {
            self.setVelocity(self.getVelocity().add(0, 0.05, 0));
        }
    }
}
