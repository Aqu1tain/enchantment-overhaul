package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
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
            int unbreakingLevel = unbreakingEquivalentLevel(temperingLevel);
            int reduced = 0;
            for (int i = 0; i < result; i++) {
                if (shouldApplyTemperedDamage(self, unbreakingLevel, world.getRandom())) reduced++;
            }
            result = reduced;
        }

        if (hasEnchantment(self, ModEnchantments.CURSE_OF_FRAGILITY)) {
            result *= 2;
        }

        cir.setReturnValue(result);
    }

    private static int unbreakingEquivalentLevel(int temperingLevel) {
        return Math.max(1, Math.round(Math.min(temperingLevel, 5) * 3.0f / 5.0f));
    }

    private static boolean shouldApplyTemperedDamage(ItemStack stack, int unbreakingLevel, Random random) {
        if (stack.isIn(ItemTags.ARMOR_ENCHANTABLE) && random.nextFloat() < 0.6f) {
            return true;
        }
        return random.nextInt(unbreakingLevel + 1) == 0;
    }

    private static boolean hasEnchantment(ItemStack stack, RegistryKey<Enchantment> key) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(key)) return true;
        }
        return false;
    }
}
