package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow @Nullable private String newItemName;
    @Shadow @Final private Property levelCost;

    private AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void replaceAnvilLogic(CallbackInfo ci) {
        ItemStack first = this.input.getStack(0);
        ItemStack second = this.input.getStack(1);

        if (first.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        ItemStack result = first.copy();
        boolean hasOperation = false;

        if (!second.isEmpty() && first.isDamageable() && first.canRepairWith(second)) {
            int damage = first.getDamage();
            int repairPerUnit = first.getMaxDamage() / 4;

            for (int i = 0; i < second.getCount() && damage > 0; i++) {
                damage = Math.max(0, damage - repairPerUnit);
            }

            if (damage < first.getDamage()) {
                result.setDamage(damage);
                hasOperation = true;
            }
        }

        if (this.newItemName != null && !this.newItemName.isBlank()) {
            if (!this.newItemName.equals(first.getName().getString())) {
                result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
                hasOperation = true;
            }
        } else if (first.contains(DataComponentTypes.CUSTOM_NAME)) {
            result.remove(DataComponentTypes.CUSTOM_NAME);
            hasOperation = true;
        }

        if (!second.isEmpty() && !hasOperation) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        if (hasOperation) {
            result.remove(DataComponentTypes.REPAIR_COST);
            this.levelCost.set(1);
            this.output.setStack(0, result);
        } else {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }

        ci.cancel();
    }
}
