package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import java.util.function.Consumer;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class LegendaryTooltipMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void appendLegendaryTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                        Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (!LegendaryItems.isLegendary(stack)) return;
        textConsumer.accept(LegendaryItems.tooltip());
    }
}
