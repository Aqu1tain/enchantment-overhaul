package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandTrampleMixin {

    @Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
    private void preventTrampleWithFeatherFalling(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity living)) return;

        ItemStack boots = living.getEquippedStack(EquipmentSlot.FEET);
        ItemEnchantmentsComponent enchantments = boots.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(Enchantments.FEATHER_FALLING)) {
                ci.cancel();
                return;
            }
        }
    }
}
