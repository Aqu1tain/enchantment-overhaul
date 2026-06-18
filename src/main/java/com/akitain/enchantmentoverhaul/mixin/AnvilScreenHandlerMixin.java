package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
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
    @Shadow private int repairItemUsage;
    @Shadow private boolean keepSecondSlot;

    private AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void replaceAnvilLogic(CallbackInfo ci) {
        ItemStack first = this.input.getStack(0);
        ItemStack second = this.input.getStack(1);

        if (first.isEmpty()) {
            clearOutput(ci);
            return;
        }

        if (LegendaryItems.isLegendary(first)) {
            clearOutput(ci);
            return;
        }

        ItemStack result = first.copy();
        int restoreCost = tryRestoreSlot(first, second, result);
        int repairUnits = tryRepair(first, second, result);
        boolean renamed = tryRename(first, result);
        boolean changed = restoreCost > 0 || repairUnits > 0 || renamed;

        if (!changed) {
            clearOutput(ci);
            return;
        }

        int unitsConsumed = repairUnits > 0 ? repairUnits : (restoreCost > 0 ? 1 : 0);
        this.repairItemUsage = unitsConsumed;
        this.keepSecondSlot = unitsConsumed == 0;

        result.remove(DataComponentTypes.REPAIR_COST);
        this.levelCost.set(Math.max(1, restoreCost));
        this.output.setStack(0, result);
        ci.cancel();
    }

    private int tryRestoreSlot(ItemStack first, ItemStack second, ItemStack result) {
        int penalty = SlotSystem.getGrindstonePenalty(first);
        if (penalty <= 0 || second.isEmpty()) return 0;

        Item repairIngot = getRepairIngot(first);
        if (repairIngot == null || !second.isOf(repairIngot)) return 0;

        result.set(ModComponents.GRINDSTONE_PENALTY, penalty - 1);
        return getRestoreCost(first);
    }

    private static Item getRepairIngot(ItemStack stack) {
        String id = Registries.ITEM.getId(stack.getItem()).getPath();
        if (id.startsWith("netherite_")) return Items.NETHERITE_INGOT;
        if (id.startsWith("diamond_")) return Items.DIAMOND;
        if (id.startsWith("golden_")) return Items.GOLD_INGOT;
        if (id.startsWith("iron_") || id.startsWith("chainmail_")) return Items.IRON_INGOT;
        if (id.startsWith("copper_")) return Items.COPPER_INGOT;
        if (id.startsWith("leather_")) return Items.LEATHER;
        if (id.startsWith("wooden_")) return Items.OAK_PLANKS;
        if (id.startsWith("stone_")) return Items.COBBLESTONE;
        return Items.IRON_INGOT;
    }

    private static int getRestoreCost(ItemStack stack) {
        String id = Registries.ITEM.getId(stack.getItem()).getPath();
        if (id.startsWith("netherite_")) return 10;
        if (id.startsWith("diamond_")) return 8;
        if (id.startsWith("golden_")) return 5;
        if (id.startsWith("iron_") || id.startsWith("chainmail_")) return 5;
        if (id.startsWith("copper_")) return 3;
        return 2;
    }

    private int tryRepair(ItemStack first, ItemStack second, ItemStack result) {
        if (second.isEmpty() || !first.isDamageable() || !first.canRepairWith(second)) return 0;

        int repairPerUnit = first.getMaxDamage() / 4;
        int damage = first.getDamage();
        if (repairPerUnit <= 0 || damage <= 0) return 0;

        int units = 0;
        while (units < second.getCount() && damage > 0) {
            damage = Math.max(0, damage - repairPerUnit);
            units++;
        }

        result.setDamage(damage);
        return units;
    }

    private boolean tryRename(ItemStack first, ItemStack result) {
        if (this.newItemName != null && !this.newItemName.isBlank()) {
            if (this.newItemName.equals(first.getName().getString())) return false;
            result.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            return true;
        }

        if (!first.contains(DataComponentTypes.CUSTOM_NAME)) return false;
        result.remove(DataComponentTypes.CUSTOM_NAME);
        return true;
    }

    private void clearOutput(CallbackInfo ci) {
        this.repairItemUsage = 0;
        this.keepSecondSlot = false;
        this.output.setStack(0, ItemStack.EMPTY);
        this.levelCost.set(0);
        ci.cancel();
    }
}
