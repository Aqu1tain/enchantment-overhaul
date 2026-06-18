package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class LegendaryNameMixin {

    @Inject(method = "getFormattedName", at = @At("RETURN"), cancellable = true)
    private void colorLegendaryName(CallbackInfoReturnable<Text> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (!LegendaryItems.isLegendary(self)) return;
        cir.setReturnValue(cir.getReturnValue().copy().styled(style -> style.withColor(LegendaryItems.COLOR)));
    }
}
