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
        // Vanilla coord system: X=0-13 (left-right), Y=0+ (up), Z=0-14 (door=0, back=14)
        // Center bookshelves are at X=3-4, 6-7, 9-10 for Z=3,5,7,9,11
        // Place enchanting setup in back-right area: around X=9-10, Z=9-11

        // Obsidian pedestal where an enchanting table would go
        this.placeBlock(world, Blocks.OBSIDIAN.defaultBlockState(), 7, 1, 10, chunkBox);

        // Clear area around pedestal (layers 1-3)
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int y = 1; y <= 3; y++) {
            // Remove center bookshelf columns near the setup
            this.placeBlock(world, air, 9, y, 9, chunkBox);
            this.placeBlock(world, air, 10, y, 9, chunkBox);
            this.placeBlock(world, air, 9, y, 11, chunkBox);
            this.placeBlock(world, air, 10, y, 11, chunkBox);

            // Clear space around obsidian
            this.placeBlock(world, air, 6, y, 10, chunkBox);
            this.placeBlock(world, air, 7, y, 10, chunkBox);
            this.placeBlock(world, air, 8, y, 10, chunkBox);
            this.placeBlock(world, air, 6, y, 11, chunkBox);
            this.placeBlock(world, air, 7, y, 11, chunkBox);
            this.placeBlock(world, air, 8, y, 11, chunkBox);
        }

        // Place chiseled bookshelves facing toward the obsidian
        placeChiseledBookshelf(world, random, 6, 1, 9, Direction.SOUTH, chunkBox);
        placeChiseledBookshelf(world, random, 9, 1, 10, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 10, 1, 10, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 8, 1, 12, Direction.NORTH, chunkBox);
    }

    @Unique
    private void placeChiseledBookshelf(WorldGenLevel world, RandomSource random, int x, int y, int z, Direction facing, BoundingBox bb) {
        BlockState state = Blocks.CHISELED_BOOKSHELF.defaultBlockState()
                .setValue(ChiseledBookShelfBlock.FACING, facing);

        int bookCount = 1 + random.nextInt(2);
        boolean[] occupied = new boolean[6];
        for (int i = 0; i < bookCount; i++) {
            occupied[random.nextInt(6)] = true;
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
