package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Item.class)
public class EnchantedBookNameMixin {

    @Inject(method = "getName(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
    private void overrideEnchantedBookName(ItemStack stack, CallbackInfoReturnable<Text> cir) {
        if ((Object) this != Items.ENCHANTED_BOOK) return;

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        if (enchantments.isEmpty()) return;

        var entry = enchantments.entrySet().iterator().next();
        Enchantment enchantment = entry.getKey();
        int level = entry.getValue();

        MutableText name = Text.translatable(enchantment.getTranslationKey()).copy();
        if (level > 1) {
            name.append(" ").append(Text.translatable("enchantment.level." + level));
        }
        MutableText bookName = Text.translatable("item.enchantment-overhaul.enchanted_book", name);

        Formatting color = enchantment.isCursed() ? Formatting.RED : Formatting.LIGHT_PURPLE;
        cir.setReturnValue(bookName.setStyle(Style.EMPTY.withColor(color)));
    }
}
