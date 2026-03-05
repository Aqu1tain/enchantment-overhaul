package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {

    @Inject(method = "grind", at = @At("RETURN"))
    private void applySlotPenalty(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty()) return;

        EnchantmentHelper.apply(result, components -> components.remove(enchantment -> true));

        int penalty = result.getOrDefault(ModComponents.GRINDSTONE_PENALTY, 0);
        result.set(ModComponents.GRINDSTONE_PENALTY, penalty + 1);
    }
}
