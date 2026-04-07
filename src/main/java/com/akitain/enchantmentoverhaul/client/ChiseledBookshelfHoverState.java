package com.akitain.enchantmentoverhaul.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.OptionalInt;

public final class ChiseledBookshelfHoverState {

    private static Component label;
    private static int width;
    private static BlockPos cachedPos;
    private static int cachedSlot = -1;
    private static ItemStack cachedStack;

    private ChiseledBookshelfHoverState() {}

    public static Component label() { return label; }
    public static int width() { return width; }

    public static void tick(Minecraft mc) {
        if (mc.options.hideGui || mc.level == null || mc.player == null) { clear(); return; }
        if (!(mc.hitResult instanceof BlockHitResult blockHit)) { clear(); return; }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChiseledBookShelfBlock shelfBlock)) { clear(); return; }

        OptionalInt hitSlot = shelfBlock.getHitSlot(blockHit, state.getValue(HorizontalDirectionalBlock.FACING));
        if (hitSlot.isEmpty()) { clear(); return; }
        int slot = hitSlot.getAsInt();

        if (!state.getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot))) { clear(); return; }
        if (!(mc.level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity shelf)) { clear(); return; }

        ItemStack stack = shelf.getItem(slot);
        if (stack.isEmpty()) { clear(); return; }

        if (stack == cachedStack && slot == cachedSlot && pos.equals(cachedPos)) return;

        Font font = mc.font;
        label = stack.getHoverName();
        width = font.width(label);
        cachedPos = pos.immutable();
        cachedSlot = slot;
        cachedStack = stack;
    }

    private static void clear() {
        if (label == null) return;
        label = null;
        cachedPos = null;
        cachedSlot = -1;
        cachedStack = null;
    }
}
