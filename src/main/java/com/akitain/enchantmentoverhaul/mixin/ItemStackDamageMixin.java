package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackDamageMixin {

    @Inject(method = "calculateDamage", at = @At("RETURN"), cancellable = true)
    private void applyTempering(int damage, ServerWorld world, ServerPlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        int level = self.getOrDefault(ModComponents.TEMPERING_LEVEL, 0);
        if (level <= 0) return;

        int remaining = cir.getReturnValue();
        int result = 0;
        for (int i = 0; i < remaining; i++) {
            if (world.getRandom().nextInt(level + 1) == 0) result++;
        }
        cir.setReturnValue(result);
    }
}
