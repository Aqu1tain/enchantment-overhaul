package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerExhaustionMixin {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float applyCurseOfHunger(float exhaustion) {
        Player self = (Player) (Object) this;
        int cursedPieces = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = self.getItemBySlot(slot);
            ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (var entry : enchantments.entrySet()) {
                if (entry.getKey().is(ModEnchantments.CURSE_OF_HUNGER)) {
                    cursedPieces++;
                    break;
                }
            }
        }
        if (cursedPieces == 0) return exhaustion;
        return exhaustion * (1.0f + 0.3f * cursedPieces);
    }
}
