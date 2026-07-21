package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class LegendaryNameMixin {

    @Inject(method = "getStyledHoverName", at = @At("RETURN"), cancellable = true)
    private void colorLegendaryName(CallbackInfoReturnable<Component> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (!LegendaryItems.isLegendary(self)) return;
        cir.setReturnValue(cir.getReturnValue().copy().withStyle(Style.EMPTY.withColor(LegendaryItems.COLOR)));
    }
}
