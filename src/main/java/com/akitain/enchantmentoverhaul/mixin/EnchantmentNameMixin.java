package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentNameMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private static void removeLevelSuffix(RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<Text> cir) {
        Formatting color = enchantment.isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.GRAY;
        MutableText name = Texts.setStyleIfAbsent(enchantment.value().description().copy(), Style.EMPTY.withColor(color));
        cir.setReturnValue(name);
    }
}
