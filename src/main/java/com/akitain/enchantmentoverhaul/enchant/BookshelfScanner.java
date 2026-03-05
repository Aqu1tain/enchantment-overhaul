package com.akitain.enchantmentoverhaul.enchant;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class BookshelfScanner {

    public record ScanResult(Set<RegistryKey<Enchantment>> unlocked, int normalBookshelves) {}

    public static ScanResult scan(World world, BlockPos tablePos) {
        Set<RegistryKey<Enchantment>> unlocked = new HashSet<>();
        int normalCount = 0;

        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            BlockPos shelfPos = tablePos.add(offset);
            BlockPos betweenPos = tablePos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2);

            if (!world.getBlockState(betweenPos).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) continue;

            BlockState state = world.getBlockState(shelfPos);

            if (state.isOf(Blocks.CHISELED_BOOKSHELF)) {
                collectEnchantments(world, shelfPos, unlocked);
            } else if (state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                normalCount++;
            }
        }

        return new ScanResult(unlocked, normalCount);
    }

    private static void collectEnchantments(World world, BlockPos pos, Set<RegistryKey<Enchantment>> unlocked) {
        if (!(world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity shelf)) return;

        for (int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i++) {
            ItemStack book = shelf.getStack(i);
            if (!book.isOf(Items.ENCHANTED_BOOK)) continue;

            ItemEnchantmentsComponent stored = book.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : stored.getEnchantmentEntries()) {
                entry.getKey().getKey().ifPresent(unlocked::add);
            }
        }
    }

    public static double reagentDiscount(int normalBookshelves) {
        int capped = Math.min(normalBookshelves, 15);
        return capped * (0.5 / 15.0);
    }
}
