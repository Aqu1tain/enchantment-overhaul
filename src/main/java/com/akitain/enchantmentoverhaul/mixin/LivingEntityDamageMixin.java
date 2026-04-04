package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float applyCustomResistances(float amount, ServerLevel world, DamageSource source, float original) {
        LivingEntity self = (LivingEntity) (Object) this;
        float result = amount * InnateMaterialProperties.getDamageMultiplier(self, source);
        result *= getWardingMultiplier(self);
        result *= getLastStandMultiplier(self);
        return result;
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static float getWardingMultiplier(LivingEntity entity) {
        int totalEpf = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            totalEpf += entity.getItemBySlot(slot).getOrDefault(ModComponents.WARDING_LEVEL, 0);
        }
        if (totalEpf <= 0) return 1.0f;
        int capped = Math.min(totalEpf, 20);
        return 1.0f - (capped * 0.032f);
    }

    private static float getLastStandMultiplier(LivingEntity entity) {
        if (entity.getHealth() > entity.getMaxHealth() * 0.2f) return 1.0f;

        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        int level = getEnchantmentLevel(chest, ModEnchantments.LAST_STAND);
        if (level <= 0) return 1.0f;

        return 1.0f - (level * 0.1f);
    }

    private static int getEnchantmentLevel(ItemStack stack, net.minecraft.resources.ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) return entry.getIntValue();
        }
        return 0;
    }
}
