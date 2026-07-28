package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Parry: blocking with an enchanted shield pushes the attacker away and costs the shield no durability.
@Mixin(LivingEntity.class)
public abstract class ParryMixin {

    @Shadow @Nullable public abstract ItemStack getItemBlockingWith();

    @Inject(method = "blockUsingItem", at = @At("HEAD"))
    private void eoParryKnockback(ServerLevel level, LivingEntity attacker, CallbackInfo ci) {
        LivingEntity defender = (LivingEntity) (Object) this;
        if (parryLevel(getItemBlockingWith()) <= 0) return;
        attacker.knockback(0.5, defender.getX() - attacker.getX(), defender.getZ() - attacker.getZ());
    }

    @WrapWithCondition(method = "applyItemBlocking",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BlocksAttacks;hurtBlockingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;F)V"))
    private boolean eoParrySkipDurability(BlocksAttacks blocksAttacks, Level level, ItemStack item, LivingEntity user, InteractionHand hand, float damage) {
        return parryLevel(item) <= 0;
    }

    @Unique
    private static int parryLevel(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(ModEnchantments.PARRY)) return entry.getIntValue();
        }
        return 0;
    }
}
