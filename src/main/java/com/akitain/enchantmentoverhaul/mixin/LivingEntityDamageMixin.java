package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float applyCustomResistances(float amount, ServerWorld world, DamageSource source, float original) {
        LivingEntity self = (LivingEntity) (Object) this;
        float result = amount * InnateMaterialProperties.getDamageMultiplier(self, source);
        result *= getWardingMultiplier(self);
        result *= getLastStandMultiplier(self);
        return result;
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final float PROTECTION_REDUCTION_PER_LEVEL = 0.04f;
    private static final float WARDING_TO_PROTECTION_SCALE = 4.0f / 5.0f;

    private static float getWardingMultiplier(LivingEntity entity) {
        int totalEpf = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            totalEpf += entity.getEquippedStack(slot).getOrDefault(ModComponents.WARDING_LEVEL, 0);
        }
        if (totalEpf <= 0) return 1.0f;
        int capped = Math.min(totalEpf, 20);
        return 1.0f - (capped * PROTECTION_REDUCTION_PER_LEVEL * WARDING_TO_PROTECTION_SCALE);
    }

    private static float getLastStandMultiplier(LivingEntity entity) {
        if (entity.getHealth() > entity.getMaxHealth() * 0.2f) return 1.0f;

        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        int level = getEnchantmentLevel(chest, ModEnchantments.LAST_STAND);
        if (level <= 0) return 1.0f;

        return 1.0f - (level * 0.1f);
    }

    private static int getEnchantmentLevel(ItemStack stack, net.minecraft.registry.RegistryKey<Enchantment> key) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(key)) return entry.getIntValue();
        }
        return 0;
    }
}
