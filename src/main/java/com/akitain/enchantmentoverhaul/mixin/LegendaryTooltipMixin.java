package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class LegendaryTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void appendLegendaryTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                        Consumer<Component> adder, TooltipFlag flag, CallbackInfo ci) {
        if (!LegendaryItems.isLegendary(stack)) return;
        adder.accept(LegendaryItems.tooltip());
    }
}
