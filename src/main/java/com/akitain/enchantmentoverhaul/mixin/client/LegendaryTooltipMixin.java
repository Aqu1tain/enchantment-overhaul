package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class LegendaryTooltipMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void appendLegendaryTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (!LegendaryItems.isLegendary(stack)) return;
        tooltip.add(LegendaryItems.tooltip());
    }
}
