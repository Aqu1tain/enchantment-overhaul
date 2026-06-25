package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class BurnishingMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void eo$stripAllCopperOxidation(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        if (!eo$hasBurnishing(stack)) return;

        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        if (Oxidizable.getDecreasedOxidationBlock(state.getBlock()).isEmpty()) return;

        int stages = 0;
        BlockState cursor = state;
        Optional<BlockState> next;
        while ((next = Oxidizable.getDecreasedOxidationState(cursor)).isPresent()) {
            cursor = next.get();
            stages++;
        }
        if (stages == 0) return;

        world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.syncWorldEvent(player, 3005, pos, 0);
        world.setBlockState(pos, cursor, 11);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, cursor));
        if (player != null && !world.isClient()) {
            stack.damage(stages, player, context.getHand().getEquipmentSlot());
        }
        cir.setReturnValue(ActionResult.SUCCESS);
    }

    @Unique
    private static boolean eo$hasBurnishing(ItemStack stack) {
        ItemEnchantmentsComponent ench = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : ench.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.BURNISHING)) return true;
        }
        return false;
    }
}
