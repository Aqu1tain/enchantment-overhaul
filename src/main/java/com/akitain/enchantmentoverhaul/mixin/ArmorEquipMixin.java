package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ArmorEquipMixin {

    // HEAD, not TAIL: vanilla's body is wrapped in early-out conditions (sound/game-event gating), so a TAIL
    // injection never runs for a plain armor swap and the advancement was never granted.
    @Inject(method = "onEquipItem", at = @At("HEAD"))
    private void onArmorChanged(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (!slot.isArmor()) return;
        if ((Object) this instanceof ServerPlayer player) {
            ModAdvancements.checkWalkingFortress(player);
        }
    }
}
