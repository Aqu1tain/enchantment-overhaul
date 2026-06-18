package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public class LegendaryMendingMixin {

    @Redirect(method = "repairPlayerGears", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;chooseEquipmentWith(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"))
    private Map.Entry<EquipmentSlot, ItemStack> excludeLegendaryFromMending(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> predicate) {
        return EnchantmentHelper.chooseEquipmentWith(enchantment, entity, stack -> predicate.test(stack) && !LegendaryItems.isLegendary(stack));
    }
}
