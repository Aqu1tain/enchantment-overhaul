package com.akitain.enchantmentoverhaul.client;

import com.akitain.enchantmentoverhaul.mixin.accessor.ChiseledBookshelfBlockInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

import java.util.Map;
import java.util.Optional;

public final class ChiseledBookshelfHoverState {

    private static Text label;
    private static int width;
    private static BlockPos cachedPos;
    private static int cachedSlot = -1;
    private static ItemStack cachedStack;

    private ChiseledBookshelfHoverState() {}

    public static Text label() { return label; }
    public static int width() { return width; }

    public static void tick(MinecraftClient mc) {
        if (mc.options.hudHidden || mc.world == null || mc.player == null) { clear(); return; }
        if (!(mc.crosshairTarget instanceof BlockHitResult blockHit)) { clear(); return; }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.world.getBlockState(pos);
        if (!(state.getBlock() instanceof ChiseledBookshelfBlock)) { clear(); return; }

        Optional<Vec2f> hitPos = ChiseledBookshelfBlockInvoker.invokeGetHitPos(blockHit, state.get(HorizontalFacingBlock.FACING));
        if (hitPos.isEmpty()) { clear(); return; }
        int slot = ChiseledBookshelfBlockInvoker.invokeGetSlotForHitPos(hitPos.get());

        if (!state.get(ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot))) { clear(); return; }
        if (!(mc.world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity shelf)) { clear(); return; }

        ItemStack stack = shelf.getStack(slot);
        if (stack.isEmpty()) { clear(); return; }

        if (stack == cachedStack && slot == cachedSlot && pos.equals(cachedPos)) return;

        TextRenderer font = mc.textRenderer;
        label = bookLabel(stack);
        width = font.getWidth(label);
        cachedPos = pos.toImmutable();
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

    private static Text bookLabel(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        if (enchantments.isEmpty()) {
            return stack.getName();
        }
        var entry = enchantments.entrySet().iterator().next();
        return entry.getKey().getName(entry.getValue());
    }
}
