package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookshelfScanner {

    public record ScanResult(Set<Enchantment> unlocked, int normalBookshelves) {}

    public static ScanResult scan(World world, BlockPos tablePos) {
        Set<Enchantment> unlocked = new HashSet<>();
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

    private static void collectEnchantments(World world, BlockPos pos, Set<Enchantment> unlocked) {
        if (!(world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity shelf)) return;

        for (int i = 0; i < ChiseledBookshelfBlockEntity.MAX_BOOKS; i++) {
            ItemStack book = shelf.getStack(i);
            if (!book.isOf(Items.ENCHANTED_BOOK)) continue;

            NbtCompound nbt = book.getNbt();
            if (nbt == null) continue;

            NbtList storedEnchantments = nbt.getList("StoredEnchantments", 10);
            for (int j = 0; j < storedEnchantments.size(); j++) {
                NbtCompound enchNbt = storedEnchantments.getCompound(j);
                Identifier id = Identifier.tryParse(enchNbt.getString("id"));
                if (id == null) continue;
                Enchantment enchantment = Registries.ENCHANTMENT.get(id);
                if (enchantment != null) unlocked.add(enchantment);
            }
        }
    }

    public static double reagentDiscount(int normalBookshelves) {
        int capped = Math.min(normalBookshelves, 15);
        return capped * (0.5 / 15.0);
    }
}
