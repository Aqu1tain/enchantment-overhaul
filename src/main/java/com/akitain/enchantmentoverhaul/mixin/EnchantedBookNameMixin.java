package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class EnchantedBookNameMixin {

    @Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private void overrideEnchantedBookName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if ((Object) this != Items.ENCHANTED_BOOK) return;

        ItemEnchantments enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return;

        var entry = enchantments.entrySet().iterator().next();
        var holder = entry.getKey();
        int level = entry.getIntValue();

        MutableComponent name = holder.value().description().copy();
        if (level > 1) {
            name.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }
        name.append(Component.literal(" Book"));

        ChatFormatting color = holder.is(EnchantmentTags.CURSE) ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE;
        cir.setReturnValue(name.withStyle(Style.EMPTY.withColor(color)));
    }
}
