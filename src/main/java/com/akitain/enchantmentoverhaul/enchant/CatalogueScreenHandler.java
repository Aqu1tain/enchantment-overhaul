package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CatalogueScreenHandler extends ScreenHandler {

    public static final int ENCHANT_BUTTON_ID = 1000;

    private final Inventory inventory = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            CatalogueScreenHandler.this.onContentChanged(this);
        }
    };

    private final ScreenHandlerContext context;
    private final DynamicRegistryManager registryManager;
    private final Set<Identifier> unlockedIds;
    private final int normalBookshelves;

    private List<CatalogueEntry> entries = List.of();
    private int selectedIndex = -1;
    private int selectedLevel = 1;

    public static CatalogueScreenHandler fromData(int syncId, PlayerInventory playerInventory, CatalogueData data) {
        return new CatalogueScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY, data.unlocked(), data.normalBookshelves());
    }

    public CatalogueScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context,
                                   List<Identifier> unlocked, int normalBookshelves) {
        super(ModScreenHandlers.CATALOGUE, syncId);
        this.context = context;
        this.registryManager = playerInventory.player.getRegistryManager();
        this.unlockedIds = Set.copyOf(unlocked);
        this.normalBookshelves = normalBookshelves;

        this.addSlot(new Slot(this.inventory, 0, 18, 22) {
            @Override
            public int getMaxItemCount() { return 1; }
        });
        this.addSlot(new Slot(this.inventory, 1, 18, 48) {
            @Override
            public boolean canInsert(ItemStack stack) { return stack.isOf(Items.LAPIS_LAZULI); }
        });
        this.addSlot(new Slot(this.inventory, 2, 18, 74));
        this.addPlayerSlots(playerInventory, 59, 148);
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (inv != this.inventory) return;
        this.selectedIndex = -1;
        this.selectedLevel = 1;
        rebuildEntries();
    }

    public void rebuildEntries() {
        ItemStack item = this.inventory.getStack(0);
        if (item.isEmpty()) {
            this.entries = List.of();
            return;
        }

        List<CatalogueEntry> result = new ArrayList<>();
        var registry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        ItemEnchantmentsComponent existing = item.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        for (RegistryEntry<Enchantment> entry : registry.getIndexedEntries()) {
            Optional<RegistryKey<Enchantment>> keyOpt = entry.getKey();
            if (keyOpt.isEmpty()) continue;

            RegistryKey<Enchantment> key = keyOpt.get();
            if (DisabledEnchantments.isDisabled(entry)) continue;
            if (!entry.value().isAcceptableItem(item)) continue;
            if (existing.getEnchantments().contains(entry)) continue;

            if (!unlockedIds.contains(key.getValue())) continue;

            int maxLevel = entry.value().getMaxLevel();
            result.add(new CatalogueEntry(entry, key, maxLevel));
        }

        this.entries = result;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == ENCHANT_BUTTON_ID) return tryEnchant(player);

        if (id >= 0 && id < 1000) {
            int index = id / 10;
            int level = (id % 10) + 1;
            if (index < entries.size()) {
                this.selectedIndex = index;
                this.selectedLevel = Math.min(level, entries.get(index).maxLevel());
                return true;
            }
        }
        return false;
    }

    private boolean tryEnchant(PlayerEntity player) {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) return false;

        CatalogueEntry entry = entries.get(selectedIndex);

        RegistryKey<Enchantment> key = entry.key();
        int level = Math.min(selectedLevel, entry.maxLevel());

        int lapisNeeded = EnchantmentCosts.lapisCost(level);
        int reagentNeeded = EnchantmentCosts.reagentCost(level, normalBookshelves);
        int xpNeeded = EnchantmentCosts.xpCost(key, level);
        int slotsNeeded = EnchantmentCosts.slotCost(key, level);

        ItemStack item = this.inventory.getStack(0);
        ItemStack lapis = this.inventory.getStack(1);
        ItemStack reagent = this.inventory.getStack(2);

        if (!canAfford(player, item, lapis, reagent, key, lapisNeeded, reagentNeeded, xpNeeded, slotsNeeded))
            return false;

        item.addEnchantment(entry.entry(), level);

        if (!player.isCreative()) {
            lapis.decrement(lapisNeeded);
            reagent.decrement(reagentNeeded);
            player.addExperienceLevels(-xpNeeded);
        }

        this.selectedIndex = -1;
        this.selectedLevel = 1;
        rebuildEntries();
        this.sendContentUpdates();
        return true;
    }

    private boolean canAfford(PlayerEntity player, ItemStack item, ItemStack lapis, ItemStack reagent,
                               RegistryKey<Enchantment> key, int lapisNeeded, int reagentNeeded, int xpNeeded, int slotsNeeded) {
        if (SlotSystem.getAvailableSlots(item) < slotsNeeded) return false;
        if (player.isCreative()) return true;
        if (lapis.getCount() < lapisNeeded) return false;
        if (reagent.getCount() < reagentNeeded) return false;
        if (!reagent.isOf(EnchantmentCosts.reagent(key))) return false;
        return player.experienceLevel >= xpNeeded;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        if (slotIndex < 3) {
            if (!this.insertItem(stack, 3, 39, true)) return ItemStack.EMPTY;
        } else if (stack.isOf(Items.LAPIS_LAZULI)) {
            if (!this.insertItem(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else {
            if (!this.insertItem(stack, 0, 1, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return stack.getCount() == copy.getCount() ? ItemStack.EMPTY : copy;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.ENCHANTING_TABLE);
    }

    public List<CatalogueEntry> getEntries() { return entries; }
    public int getSelectedIndex() { return selectedIndex; }
    public int getSelectedLevel() { return selectedLevel; }
    public int getNormalBookshelves() { return normalBookshelves; }
    public Set<Identifier> getUnlockedIds() { return unlockedIds; }

    public void setSelection(int index, int level) {
        this.selectedIndex = index;
        this.selectedLevel = level;
    }

    public record CatalogueEntry(RegistryEntry<Enchantment> entry, RegistryKey<Enchantment> key, int maxLevel) {}
}
