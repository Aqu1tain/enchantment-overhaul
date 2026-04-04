package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class ItemStackDamageMixin {

    @ModifyVariable(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int applyDurabilityModifiers(int amount, int amountArg, Random random, ServerPlayerEntity player) {
        ItemStack self = (ItemStack) (Object) this;

        int temperingLevel = ModComponents.getInt(self, ModComponents.TEMPERING_LEVEL, 0);
        if (temperingLevel > 0) {
            int reduced = 0;
            for (int i = 0; i < amount; i++) {
                if (random.nextInt(temperingLevel + 1) == 0) reduced++;
            }
            amount = reduced;
        }

        if (EnchantmentHelper.getLevel(ModEnchantments.CURSE_OF_FRAGILITY, self) > 0) {
            amount *= 2;
        }

        return amount;
    }
}
