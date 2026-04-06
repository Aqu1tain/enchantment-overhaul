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
        // Exact diff computed from NBT structure vs vanilla generation
        // Vanilla coords: X=0-13, Y=0+, Z=0-14. Door at X=4, Z=0.
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState bookshelf = Blocks.BOOKSHELF.defaultBlockState();

        // Obsidian pedestal
        this.placeBlock(world, Blocks.OBSIDIAN.defaultBlockState(), 10, 1, 7, chunkBox);

        // Remove bookshelves to clear enchanting area
        // Z=9 column
        this.placeBlock(world, air, 9, 2, 9, chunkBox);
        this.placeBlock(world, air, 9, 3, 9, chunkBox);
        this.placeBlock(world, air, 10, 3, 9, chunkBox);

        // Z=7 column
        this.placeBlock(world, air, 9, 1, 7, chunkBox);
        this.placeBlock(world, air, 10, 1, 7, chunkBox);
        this.placeBlock(world, air, 9, 2, 7, chunkBox);
        this.placeBlock(world, air, 10, 2, 7, chunkBox);
        this.placeBlock(world, air, 9, 3, 7, chunkBox);
        this.placeBlock(world, air, 10, 3, 7, chunkBox);

        // Z=5 column
        this.placeBlock(world, air, 9, 2, 5, chunkBox);
        this.placeBlock(world, air, 9, 3, 5, chunkBox);
        this.placeBlock(world, air, 10, 3, 5, chunkBox);

        // Add bookshelves where vanilla had air
        this.placeBlock(world, bookshelf, 11, 1, 9, chunkBox);
        this.placeBlock(world, bookshelf, 11, 2, 9, chunkBox);
        this.placeBlock(world, bookshelf, 11, 2, 5, chunkBox);

        // Place chiseled bookshelves
        placeChiseledBookshelf(world, random, 10, 1, 9, Direction.NORTH, chunkBox);
        placeChiseledBookshelf(world, random, 12, 1, 7, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 12, 1, 6, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 11, 1, 5, Direction.SOUTH, chunkBox);
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
