package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModAdvancements;
import com.akitain.enchantmentoverhaul.smithing.SmithingTemplates;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    @Unique
    private UpgradeType pendingType = null;

    private SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void applyCustomUpgrade(CallbackInfo ci) {
        pendingType = null;

        ItemStack template = this.input.getStack(0);
        ItemStack base = this.input.getStack(1);
        ItemStack material = this.input.getStack(2);

        if (template.isEmpty() || base.isEmpty() || material.isEmpty()) return;

        UpgradeType type = SmithingTemplates.getType(template.getItem());
        if (type == null) return;

        int level = SmithingTemplates.getMaterialLevel(material.getItem());
        if (level == 0) return;
        if (!type.appliesTo(base)) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }
        if (type.currentLevel(base) >= level) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        ItemStack result = base.copy();
        type.applyTo(result, level);
        this.output.setStack(0, result);
        pendingType = type;
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    private void grantAdvancement(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (pendingType != null && player instanceof ServerPlayerEntity serverPlayer) {
            ModAdvancements.grantSmithingAdvancement(serverPlayer, pendingType);
        }
        pendingType = null;
    }
}
