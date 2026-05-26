package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackDamageMixin {

    @Inject(method = "processDurabilityChange", at = @At("RETURN"), cancellable = true)
    private void applyDurabilityModifiers(int damage, ServerLevel world, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
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

    private static boolean shouldApplyTemperedDamage(ItemStack stack, int unbreakingLevel, RandomSource random) {
        if (stack.is(ItemTags.ARMOR_ENCHANTABLE) && random.nextFloat() < 0.6f) {
            return true;
        }
        return random.nextInt(unbreakingLevel + 1) == 0;
    }

    private static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) return true;
        }
        return false;
    }
}
