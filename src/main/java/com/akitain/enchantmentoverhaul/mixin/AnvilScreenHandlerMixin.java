package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.LegendaryItems;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Shadow @Nullable private String itemName;
    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;
    @Shadow private boolean onlyRenaming;

    private AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void replaceAnvilLogic(CallbackInfo ci) {
        ItemStack first = this.inputSlots.getItem(0);
        ItemStack second = this.inputSlots.getItem(1);

        if (first.isEmpty()) return;

        if (LegendaryItems.isLegendary(first)) {
            clearOutput(ci);
            return;
        }

        // Enchanting stays exclusive to the Catalogue: never merge enchanted books on the anvil.
        if (second.is(Items.ENCHANTED_BOOK)) {
            clearOutput(ci);
            return;
        }

        ItemStack result = first.copy();
        int restoreCost = tryRestoreSlot(first, second, result);
        int repairUnits = tryRepair(first, second, result);

        // Not a mod-handled operation: let vanilla and other mods run (combine two items, modded/datapack repairs, rename).
        if (restoreCost <= 0 && repairUnits <= 0) return;

        tryRename(first, result);

        int unitsConsumed = repairUnits > 0 ? repairUnits : 1;
        this.repairItemCountCost = unitsConsumed;
        this.onlyRenaming = false;

        result.remove(DataComponents.REPAIR_COST);
        this.cost.set(Math.max(1, restoreCost));
        this.resultSlots.setItem(0, result);
        ci.cancel();
    }

    private int tryRestoreSlot(ItemStack first, ItemStack second, ItemStack result) {
        int penalty = SlotSystem.getGrindstonePenalty(first);
        if (penalty <= 0 || second.isEmpty()) return 0;

        Item repairIngot = getRepairIngot(first);
        if (repairIngot == null || !second.is(repairIngot)) return 0;

        result.set(ModComponents.GRINDSTONE_PENALTY, penalty - 1);
        return getRestoreCost(first);
    }

    private static Item getRepairIngot(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
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
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (id.startsWith("netherite_")) return 10;
        if (id.startsWith("diamond_")) return 8;
        if (id.startsWith("golden_")) return 5;
        if (id.startsWith("iron_") || id.startsWith("chainmail_")) return 5;
        if (id.startsWith("copper_")) return 3;
        return 2;
    }

    private int tryRepair(ItemStack first, ItemStack second, ItemStack result) {
        if (second.isEmpty() || !first.isDamageableItem()) return 0;
        if (!first.isValidRepairItem(second) && !isCombineOnlyRepairItem(first, second)) return 0;

        int repairPerUnit = first.getMaxDamage() / 4;
        int damage = first.getDamageValue();
        if (repairPerUnit <= 0 || damage <= 0) return 0;

        int units = 0;
        while (units < second.getCount() && damage > 0) {
            damage = Math.max(0, damage - repairPerUnit);
            units++;
        }

        result.setDamageValue(damage);
        return units;
    }

    // Items vanilla can only repair by combining two of them (no repair ingredient): give them a material repair
    // so players don't have to sacrifice a second copy. Mending and vanilla combine still work too.
    private static boolean isCombineOnlyRepairItem(ItemStack stack, ItemStack material) {
        Item repairMaterial = combineOnlyRepairMaterial(stack.getItem());
        return repairMaterial != null && material.is(repairMaterial);
    }

    private static Item combineOnlyRepairMaterial(Item item) {
        if (item == Items.BOW
                || item == Items.CROSSBOW
                || item == Items.FISHING_ROD
                || item == Items.CARROT_ON_A_STICK
                || item == Items.WARPED_FUNGUS_ON_A_STICK) return Items.STRING;
        if (item == Items.SHEARS || item == Items.FLINT_AND_STEEL) return Items.IRON_INGOT;
        if (item == Items.BRUSH) return Items.COPPER_INGOT;
        if (item == Items.TRIDENT) return Items.PRISMARINE_SHARD;
        return null;
    }

    private boolean tryRename(ItemStack first, ItemStack result) {
        if (this.itemName != null && !this.itemName.isBlank()) {
            if (this.itemName.equals(first.getHoverName().getString())) return false;
            result.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            return true;
        }

        if (!first.has(DataComponents.CUSTOM_NAME)) return false;
        result.remove(DataComponents.CUSTOM_NAME);
        return true;
    }

    private void clearOutput(CallbackInfo ci) {
        this.repairItemCountCost = 0;
        this.onlyRenaming = false;
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        this.cost.set(0);
        ci.cancel();
    }
}
