package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class GrindingSpeedMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void applyGrindingBonus(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ItemStack tool = self.getMainHandStack();
        int level = ModComponents.getInt(tool, ModComponents.GRINDING_LEVEL, 0);
        if (level <= 0) return;

        cir.setReturnValue(cir.getReturnValue() + level * 5.0f);
    }
}
