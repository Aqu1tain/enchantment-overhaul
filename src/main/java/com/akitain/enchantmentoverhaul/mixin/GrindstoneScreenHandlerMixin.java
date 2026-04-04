package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {

    @Inject(method = "grind", at = @At("RETURN"))
    private void applySlotPenalty(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty()) return;

        EnchantmentHelper.set(Map.of(), result);

        int penalty = ModComponents.getInt(result, ModComponents.GRINDSTONE_PENALTY, 0);
        ModComponents.setInt(result, ModComponents.GRINDSTONE_PENALTY, penalty + 1);
    }
}
