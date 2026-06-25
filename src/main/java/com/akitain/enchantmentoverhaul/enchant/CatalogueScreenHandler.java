package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.*;

public class CatalogueScreenHandler extends ScreenHandler {

    private final Inventory inputInventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            CatalogueScreenHandler.this.onContentChanged(this);
        }
    };
    private final Inventory outputInventory = new SimpleInventory(1);

    private final ScreenHandlerContext context;
    private final PlayerEntity player;
    private final Set<Identifier> unlockedIds;
    private final int normalBookshelves;

    private List<CatalogueEntry> entries = List.of();
    private int selectedIndex = -1;
    private int selectedLevel = 1;

    public static CatalogueScreenHandler fromBuf(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        CatalogueData data = CatalogueData.read(buf);
        return new CatalogueScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY, data.unlocked(), data.normalBookshelves());
    }

    public CatalogueScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context,
                                   List<Identifier> unlocked, int normalBookshelves) {
        super(ModScreenHandlers.CATALOGUE, syncId);
        this.context = context;
        this.player = playerInventory.player;
        this.unlockedIds = Set.copyOf(unlocked);
        this.normalBookshelves = normalBookshelves;

        this.addSlot(new Slot(this.inputInventory, 0, 9, 60) {
            @Override
            public int getMaxItemCount() { return 1; }
            @Override
            public boolean canInsert(ItemStack stack) { return SlotSystem.getBaseMaxSlots(stack) > 0; }
        });
        this.addSlot(new Slot(this.inputInventory, 1, 31, 60) {
            @Override
            public boolean canInsert(ItemStack stack) { return EnchantmentCosts.isReagent(stack.getItem()); }
        });

        this.addSlot(new Slot(this.outputInventory, 0, 20, 86) {
            @Override
            public boolean canInsert(ItemStack stack) { return false; }

            @Override
            public boolean canTakeItems(PlayerEntity player) {
                return CatalogueScreenHandler.this.canTakeOutput(player);
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                CatalogueScreenHandler.this.onOutputTaken(player);
                super.onTakeItem(player, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 7 + col * 18, 120 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 7 + col * 18, 120 + 58));
        }
    }

    @Override
    public void onContentChanged(Inventory inv) {
        if (inv != this.inputInventory) return;
        this.selectedIndex = -1;
        this.selectedLevel = 1;
        rebuildEntries();
        updateResult();
    }

    public void rebuildEntries() {
        ItemStack item = this.inputInventory.getStack(0);
        if (item.isEmpty() || LegendaryItems.isLegendary(item)) {
            this.entries = List.of();
            return;
        }

        Map<Enchantment, Integer> existing = EnchantmentHelper.get(item);
        List<CatalogueEntry> result = new ArrayList<>();

        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (DisabledEnchantments.isDisabled(enchantment)) continue;
            if (!enchantment.isAcceptableItem(item)) continue;

            Identifier id = Registries.ENCHANTMENT.getId(enchantment);
            if (id == null || !unlockedIds.contains(id)) continue;

            int maxLevel = enchantment.getMaxLevel();
            int current = existing.getOrDefault(enchantment, 0);
            if (current > 0) {
                if (current >= maxLevel) continue;
                if (enchantment.isCursed()) continue;
            } else if (conflictsWithExisting(enchantment, existing.keySet())) {
                continue;
            }
            result.add(new CatalogueEntry(enchantment, id, maxLevel, current));
        }

        this.entries = result;
    }

    private static boolean conflictsWithExisting(Enchantment candidate, java.util.Set<Enchantment> existing) {
        for (Enchantment other : existing) {
            if (!candidate.canCombine(other)) return true;
        }
        return false;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < 1000) {
            int index = id / 10;
            int level = (id % 10) + 1;
            if (index < entries.size()) {
                this.selectedIndex = index;
                CatalogueEntry clicked = entries.get(index);
                this.selectedLevel = Math.max(clicked.currentLevel() + 1, Math.min(level, clicked.maxLevel()));
                updateResult();
                return true;
            }
        }
        return false;
    }

    private void updateResult() {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) {
            outputInventory.setStack(0, ItemStack.EMPTY);
            return;
        }

        ItemStack item = inputInventory.getStack(0);
        if (item.isEmpty() || LegendaryItems.isLegendary(item)) {
            outputInventory.setStack(0, ItemStack.EMPTY);
            return;
        }

        CatalogueEntry entry = entries.get(selectedIndex);
        int level = Math.max(entry.currentLevel() + 1, Math.min(selectedLevel, entry.maxLevel()));

        int slotsNeeded = EnchantmentCosts.slotCost(entry.enchantment(), level, entry.currentLevel());
        if (SlotSystem.getAvailableSlots(item) < slotsNeeded) {
            outputInventory.setStack(0, ItemStack.EMPTY);
            return;
        }

        if (!canAfford(entry, level)) {
            outputInventory.setStack(0, ItemStack.EMPTY);
            return;
        }

        ItemStack result = item.copy();
        Map<Enchantment, Integer> resultEnchants = EnchantmentHelper.get(result);
        resultEnchants.put(entry.enchantment(), level);
        EnchantmentHelper.set(resultEnchants, result);
        outputInventory.setStack(0, result);
    }

    private boolean canAfford(CatalogueEntry entry, int level) {
        if (this.player.isCreative()) return true;

        ItemStack reagent = inputInventory.getStack(1);
        Enchantment enchantment = entry.enchantment();
        return reagent.isOf(EnchantmentCosts.reagent(enchantment))
                && reagent.getCount() >= EnchantmentCosts.reagentCost(level, entry.currentLevel(), normalBookshelves)
                && this.player.experienceLevel >= EnchantmentCosts.xpCost(enchantment, level, entry.currentLevel());
    }

    private boolean canTakeOutput(PlayerEntity player) {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) return false;
        CatalogueEntry entry = entries.get(selectedIndex);
        int level = Math.max(entry.currentLevel() + 1, Math.min(selectedLevel, entry.maxLevel()));

        ItemStack item = inputInventory.getStack(0);
        if (SlotSystem.getAvailableSlots(item) < EnchantmentCosts.slotCost(entry.enchantment(), level, entry.currentLevel())) return false;
        return canAfford(entry, level);
    }

    private void onOutputTaken(PlayerEntity player) {
        if (selectedIndex < 0 || selectedIndex >= entries.size()) return;

        CatalogueEntry entry = entries.get(selectedIndex);
        int level = Math.max(entry.currentLevel() + 1, Math.min(selectedLevel, entry.maxLevel()));
        Enchantment enchantment = entry.enchantment();

        if (!player.isCreative()) {
            inputInventory.getStack(1).decrement(EnchantmentCosts.reagentCost(level, entry.currentLevel(), normalBookshelves));
            player.addExperienceLevels(-EnchantmentCosts.xpCost(enchantment, level, entry.currentLevel()));
        }

        inputInventory.setStack(0, ItemStack.EMPTY);

        this.context.run((world, pos) ->
                world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f));

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (enchantment.isCursed()) {
                ModAdvancements.grantCurseAdvancement(serverPlayer);
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        if (slotIndex == 2) {
            if (!canTakeOutput(player)) return ItemStack.EMPTY;
            if (!this.insertItem(stack, 3, 39, true)) return ItemStack.EMPTY;
            onOutputTaken(player);
        } else if (slotIndex < 2) {
            if (!this.insertItem(stack, 3, 39, true)) return ItemStack.EMPTY;
        } else {
            if (!this.insertItem(stack, 0, 1, false))
                if (!this.insertItem(stack, 1, 2, false))
                    return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return stack.getCount() == copy.getCount() ? ItemStack.EMPTY : copy;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inputInventory));
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
        updateResult();
    }

    public record CatalogueEntry(Enchantment enchantment, Identifier id, int maxLevel, int currentLevel) {}
}
