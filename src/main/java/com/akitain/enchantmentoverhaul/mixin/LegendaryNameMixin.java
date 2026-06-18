package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.Item;

@Mixin(Item.class)
public class LegendaryNameMixin {

    @Inject(method = "getName(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void colorLegendaryName(ItemStack stack, CallbackInfoReturnable<Text> cir) {
        if (!LegendaryItems.isLegendary(stack)) return;
        cir.setReturnValue(cir.getReturnValue().copy().setStyle(Style.EMPTY.withColor(LegendaryItems.COLOR)));
    }
}
