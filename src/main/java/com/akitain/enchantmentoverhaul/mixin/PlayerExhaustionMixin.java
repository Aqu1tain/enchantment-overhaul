package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerExhaustionMixin {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float applyCurseOfHunger(float exhaustion) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        int cursedPieces = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = self.getEquippedStack(slot);
            if (EnchantmentHelper.getLevel(ModEnchantments.CURSE_OF_HUNGER, stack) > 0) {
                cursedPieces++;
            }
        }
        if (cursedPieces == 0) return exhaustion;
        return exhaustion * (1.0f + 0.3f * cursedPieces);
    }
}
