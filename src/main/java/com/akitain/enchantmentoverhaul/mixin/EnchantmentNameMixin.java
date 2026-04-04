package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.enchantment.Enchantment;
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
    private void hideLevelOneOnly(int level, CallbackInfoReturnable<Text> cir) {
        Enchantment self = (Enchantment) (Object) this;
        Formatting color = self.isCursed() ? Formatting.RED : Formatting.GRAY;
        MutableText name = Texts.setStyleIfAbsent(
                Text.translatable(self.getTranslationKey()).copy(), Style.EMPTY.withColor(color));

        if (level > 1) {
            name.append(" ").append(Text.translatable("enchantment.level." + level));
        }

        cir.setReturnValue(name);
    }
}
