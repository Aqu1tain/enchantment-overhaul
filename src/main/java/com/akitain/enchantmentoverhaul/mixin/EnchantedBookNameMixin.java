package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class EnchantedBookNameMixin {

    @Inject(method = "getName(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
    private void overrideEnchantedBookName(ItemStack stack, CallbackInfoReturnable<Text> cir) {
        if ((Object) this != Items.ENCHANTED_BOOK) return;

        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return;

        var entry = enchantments.getEnchantmentEntries().iterator().next();
        var holder = entry.getKey();
        int level = entry.getIntValue();

        MutableText name = holder.value().description().copy();
        if (level > 1) {
            name.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + level));
        }
        MutableText bookName = Text.translatable("item.enchantment-overhaul.enchanted_book", name);

        Formatting color = holder.isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.LIGHT_PURPLE;
        cir.setReturnValue(bookName.setStyle(Style.EMPTY.withColor(color)));
    }
}
