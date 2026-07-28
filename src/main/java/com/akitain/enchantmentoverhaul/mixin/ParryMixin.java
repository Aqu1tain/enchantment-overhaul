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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Parry: blocking within a short window right after raising an enchanted shield pushes the attacker
// away and costs the shield no durability. Holding the shield up past the window blocks as usual.
@Mixin(LivingEntity.class)
public abstract class ParryMixin {

    // TEMPORARY debug logging while tuning the parry window; remove before merging.
    @Unique
    private static final Logger EO_PARRY_LOG = LoggerFactory.getLogger("EO-Parry");

    @Unique
    private static final int PARRY_WINDOW_TICKS = 15;
    @Unique
    private static final double PARRY_KNOCKBACK = 1.0;

    @Shadow @Nullable public abstract ItemStack getItemBlockingWith();

    @Shadow public abstract int getTicksUsingItem();

    @Inject(method = "blockUsingItem", at = @At("HEAD"))
    private void eoParryKnockback(ServerLevel level, LivingEntity attacker, CallbackInfo ci) {
        ItemStack blocking = getItemBlockingWith();
        boolean parried = isParrying(blocking);
        int held = getTicksUsingItem();
        int limit = windowLimit(blocking);
        EO_PARRY_LOG.info("[EO-PARRY] melee block by {} | enchanted={} | held={}t | window<={}t | PARRY={}",
                ((LivingEntity) (Object) this).getName().getString(),
                parryLevel(blocking) > 0, held, limit, parried ? "YES" : "no (too late)");
        if (!parried) return;
        LivingEntity defender = (LivingEntity) (Object) this;
        attacker.knockback(PARRY_KNOCKBACK, defender.getX() - attacker.getX(), defender.getZ() - attacker.getZ());
        EO_PARRY_LOG.info("[EO-PARRY] -> knocked {} back with strength {}", attacker.getName().getString(), PARRY_KNOCKBACK);
    }

    @WrapWithCondition(method = "applyItemBlocking",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BlocksAttacks;hurtBlockingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;F)V"))
    private boolean eoParrySkipDurability(BlocksAttacks blocksAttacks, Level level, ItemStack item, LivingEntity user, InteractionHand hand, float damage) {
        boolean parried = isParrying(item);
        if (parryLevel(item) > 0) {
            EO_PARRY_LOG.info("[EO-PARRY] durability on block | held={}t | window<={}t | spared={}",
                    getTicksUsingItem(), windowLimit(item), parried);
        }
        return !parried;
    }

    @Unique
    private int windowLimit(@Nullable ItemStack stack) {
        if (stack == null) return -1;
        BlocksAttacks ba = stack.get(DataComponents.BLOCKS_ATTACKS);
        return ba == null ? -1 : ba.blockDelayTicks() + PARRY_WINDOW_TICKS;
    }

    // The window opens once the shield actually starts blocking and lasts PARRY_WINDOW_TICKS.
    @Unique
    private boolean isParrying(@Nullable ItemStack stack) {
        if (parryLevel(stack) <= 0) return false;
        BlocksAttacks blocksAttacks = stack.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks == null) return false;
        int heldFor = getTicksUsingItem();
        return heldFor <= blocksAttacks.blockDelayTicks() + PARRY_WINDOW_TICKS;
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
