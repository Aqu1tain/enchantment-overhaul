package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import com.akitain.enchantmentoverhaul.mixin.accessor.EntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class StepUpMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void applyStepUp(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        EntityAccessor accessor = (EntityAccessor) self;
        boolean shouldApply = hasStepUp(self) && !self.isSneaking();

        if (shouldApply) {
            accessor.setStepHeight(1.0f);
        } else if (accessor.getStepHeight() > 0.6f) {
            accessor.setStepHeight(0.6f);
        }
    }

    @Unique
    private static boolean hasStepUp(LivingEntity entity) {
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(ModEnchantments.STEP_UP, boots) > 0;
    }
}
