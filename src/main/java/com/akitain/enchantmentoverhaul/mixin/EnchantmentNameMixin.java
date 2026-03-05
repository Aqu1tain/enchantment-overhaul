package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentNameMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private static void removeLevelSuffix(RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<Text> cir) {
        MutableText name = enchantment.value().description().copy();
        if (enchantment.isIn(net.minecraft.registry.tag.EnchantmentTags.CURSE)) {
            name = net.minecraft.text.Texts.setStyleIfAbsent(name, net.minecraft.text.Style.EMPTY.withColor(net.minecraft.util.Formatting.RED));
        } else {
            name = net.minecraft.text.Texts.setStyleIfAbsent(name, net.minecraft.text.Style.EMPTY.withColor(net.minecraft.util.Formatting.GRAY));
        }
        cir.setReturnValue(name);
    }
}
