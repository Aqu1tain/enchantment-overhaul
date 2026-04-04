package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class StepUpMixin extends Entity {

    private StepUpMixin() { super(null, null); }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void applyStepUp(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        boolean shouldApply = hasStepUp(self) && !self.isSneaking();

        // 0.6 is vanilla default step height
        if (shouldApply) {
            this.stepHeight = 1.0f;
        } else if (this.stepHeight > 0.6f) {
            this.stepHeight = 0.6f;
        }
    }

    @Unique
    private static boolean hasStepUp(LivingEntity entity) {
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(ModEnchantments.STEP_UP, boots) > 0;
    }
}
