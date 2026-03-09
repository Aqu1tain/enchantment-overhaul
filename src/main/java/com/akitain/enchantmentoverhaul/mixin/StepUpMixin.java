package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class StepUpMixin {

    @Unique
    private static final Identifier STEP_UP_ID = Identifier.of("enchantment-overhaul", "step_up");

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void applyStepUp(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        EntityAttributeInstance attr = self.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr == null) return;

        boolean shouldApply = hasStepUp(self) && !self.isSneaking();

        if (shouldApply && attr.getModifier(STEP_UP_ID) == null) {
            attr.addTemporaryModifier(new EntityAttributeModifier(STEP_UP_ID, 0.4, EntityAttributeModifier.Operation.ADD_VALUE));
        } else if (!shouldApply && attr.getModifier(STEP_UP_ID) != null) {
            attr.removeModifier(STEP_UP_ID);
        }
    }

    @Unique
    private static boolean hasStepUp(LivingEntity entity) {
        ItemStack boots = entity.getEquippedStack(EquipmentSlot.FEET);
        ItemEnchantmentsComponent enchantments = boots.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.STEP_UP)) return true;
        }
        return false;
    }
}
