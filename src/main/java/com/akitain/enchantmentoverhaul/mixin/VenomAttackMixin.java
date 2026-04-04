package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class VenomAttackMixin {

    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void applyVenom(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!(target instanceof LivingEntity victim)) return;

        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack weapon = self.getMainHandStack();
        int level = EnchantmentHelper.getLevel(ModEnchantments.VENOM, weapon);
        if (level <= 0) return;

        int duration = 80 + 40 * (level - 1);
        victim.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, duration, 0));
    }
}
