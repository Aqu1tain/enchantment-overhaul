package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetPredicate.class)
public class VeilDetectionMixin {

    @Shadow private double baseMaxDistance;
    @Shadow private boolean useDistanceScalingFactor;

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void applyVeil(ServerWorld world, LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (attacker == null || target == null) return;

        ItemStack helmet = target.getEquippedStack(EquipmentSlot.HEAD);
        ItemEnchantmentsComponent enchantments = helmet.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        boolean hasVeil = false;
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.VEIL)) { hasVeil = true; break; }
        }
        if (!hasVeil) return;

        double range = this.baseMaxDistance;
        if (this.useDistanceScalingFactor) {
            double followRange = attacker.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
            range = Math.min(range, followRange);
        }

        double reducedRange = range * 0.25;
        if (attacker.distanceTo(target) > reducedRange) {
            cir.setReturnValue(false);
        }
    }
}
