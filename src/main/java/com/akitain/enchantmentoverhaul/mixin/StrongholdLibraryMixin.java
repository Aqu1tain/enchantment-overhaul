package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StrongholdPieces.Library.class)
public abstract class StrongholdLibraryMixin extends StructurePiece {

    private StrongholdLibraryMixin() { super(null, 0, null); }

    @Unique
    private static final List<ResourceKey<Enchantment>> BOOK_POOL = List.of(
            Enchantments.FEATHER_FALLING, ModEnchantments.STEP_UP, Enchantments.KNOCKBACK,
            Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE, Enchantments.FROST_WALKER,
            Enchantments.INFINITY, Enchantments.FORTUNE
    );

    @Inject(method = "postProcess", at = @At("TAIL"))
    private void modifyLibraryGround(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        this.placeBlock(world, Blocks.OBSIDIAN.defaultBlockState(), 7, 1, 10, chunkBox);

        clearEnchantingArea(world, chunkBox);

        placeChiseledBookshelf(world, random, 5, 2, 10, Direction.EAST, chunkBox);
        placeChiseledBookshelf(world, random, 7, 2, 12, Direction.NORTH, chunkBox);
        placeChiseledBookshelf(world, random, 8, 2, 12, Direction.NORTH, chunkBox);
        placeChiseledBookshelf(world, random, 9, 2, 11, Direction.WEST, chunkBox);
    }

    @Unique
    private void clearEnchantingArea(WorldGenLevel world, BoundingBox bb) {
        BlockState air = Blocks.AIR.defaultBlockState();

        for (int y = 2; y <= 4; y++) {
            for (int x = 6; x <= 8; x++) {
                for (int z = 9; z <= 11; z++) {
                    this.placeBlock(world, air, x, y, z, bb);
                }
            }
            this.placeBlock(world, air, 5, y, 9, bb);
            this.placeBlock(world, air, 9, y, 9, bb);
            this.placeBlock(world, air, 9, y, 10, bb);
        }

        this.placeBlock(world, air, 5, 4, 10, bb);
    }

    @Unique
    private void placeChiseledBookshelf(WorldGenLevel world, RandomSource random, int x, int y, int z, Direction facing, BoundingBox bb) {
        BlockState state = Blocks.CHISELED_BOOKSHELF.defaultBlockState()
                .setValue(ChiseledBookShelfBlock.FACING, facing);

        int bookCount = 1 + random.nextInt(2);
        boolean[] occupied = new boolean[6];
        for (int i = 0; i < bookCount; i++) {
            int slot = random.nextInt(6);
            occupied[slot] = true;
        }

        for (int i = 0; i < 6; i++) {
            if (occupied[i]) {
                state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i), true);
            }
        }

        this.placeBlock(world, state, x, y, z, bb);

        BlockPos worldPos = this.getWorldPos(x, y, z);
        if (bb.isInside(worldPos) && world.getBlockEntity(worldPos) instanceof ChiseledBookShelfBlockEntity shelf) {
            for (int slot = 0; slot < 6; slot++) {
                if (occupied[slot]) {
                    shelf.setItemNoUpdate(slot, createEnchantedBook(world, random));
                }
            }
        }
    }

    @Unique
    private static ItemStack createEnchantedBook(WorldGenLevel world, RandomSource random) {
        ResourceKey<Enchantment> key = BOOK_POOL.get(random.nextInt(BOOK_POOL.size()));
        var registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var holder = registry.getOrThrow(key);

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        builder.upgrade(holder, 1);
        book.set(DataComponents.STORED_ENCHANTMENTS, builder.toImmutable());
        return book;
    }
}
