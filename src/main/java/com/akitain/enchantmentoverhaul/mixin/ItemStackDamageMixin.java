package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackDamageMixin {

    @Inject(method = "calculateDamage", at = @At("RETURN"), cancellable = true)
    private void applyDurabilityModifiers(int damage, ServerWorld world, ServerPlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        int result = cir.getReturnValue();

        int temperingLevel = self.getOrDefault(ModComponents.TEMPERING_LEVEL, 0);
        if (temperingLevel > 0) {
            int reduced = 0;
            for (int i = 0; i < result; i++) {
                if (world.getRandom().nextInt(temperingLevel + 1) == 0) reduced++;
            }
            result = reduced;
        }

        if (hasEnchantment(self, ModEnchantments.CURSE_OF_FRAGILITY)) {
            result *= 2;
        }

        cir.setReturnValue(result);
    }

    private static boolean hasEnchantment(ItemStack stack, RegistryKey<Enchantment> key) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(key)) return true;
        }
        return false;
    }
}
