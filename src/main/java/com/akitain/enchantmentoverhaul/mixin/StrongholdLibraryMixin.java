package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import com.akitain.enchantmentoverhaul.mixin.accessor.ChiseledBookshelfBlockEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StrongholdGenerator.Library.class)
public abstract class StrongholdLibraryMixin extends StructurePiece {

    private StrongholdLibraryMixin() { super(null, 0, null); }

    @Unique
    private static final List<RegistryKey<Enchantment>> BOOK_POOL = List.of(
            Enchantments.FEATHER_FALLING, ModEnchantments.STEP_UP, Enchantments.KNOCKBACK,
            Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE, Enchantments.FROST_WALKER,
            Enchantments.INFINITY, Enchantments.FORTUNE
    );

    @Inject(method = "generate", at = @At("TAIL"))
    private void modifyLibraryGround(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot, CallbackInfo ci) {
        BlockState air = Blocks.AIR.getDefaultState();
        BlockState bookshelf = Blocks.BOOKSHELF.getDefaultState();

        this.addBlock(world, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 7, chunkBox);

        this.addBlock(world, air, 9, 2, 9, chunkBox);
        this.addBlock(world, air, 9, 3, 9, chunkBox);
        this.addBlock(world, air, 10, 3, 9, chunkBox);

        this.addBlock(world, air, 9, 1, 7, chunkBox);
        this.addBlock(world, air, 10, 1, 7, chunkBox);
        this.addBlock(world, air, 9, 2, 7, chunkBox);
        this.addBlock(world, air, 10, 2, 7, chunkBox);
        this.addBlock(world, air, 9, 3, 7, chunkBox);
        this.addBlock(world, air, 10, 3, 7, chunkBox);

        this.addBlock(world, air, 9, 2, 5, chunkBox);
        this.addBlock(world, air, 9, 3, 5, chunkBox);
        this.addBlock(world, air, 10, 3, 5, chunkBox);

        this.addBlock(world, bookshelf, 11, 1, 9, chunkBox);
        this.addBlock(world, bookshelf, 11, 2, 9, chunkBox);
        this.addBlock(world, bookshelf, 11, 2, 5, chunkBox);

        placeChiseledBookshelf(world, random, 10, 1, 9, Direction.SOUTH, chunkBox);
        placeChiseledBookshelf(world, random, 12, 1, 7, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 12, 1, 6, Direction.WEST, chunkBox);
        placeChiseledBookshelf(world, random, 11, 1, 5, Direction.NORTH, chunkBox);
    }

    @Unique
    private void placeChiseledBookshelf(StructureWorldAccess world, Random random, int x, int y, int z, Direction facing, BlockBox bb) {
        BlockState state = Blocks.CHISELED_BOOKSHELF.getDefaultState()
                .with(ChiseledBookshelfBlock.FACING, facing);

        int bookCount = 1;
        boolean[] occupied = new boolean[6];
        for (int i = 0; i < bookCount; i++) {
            occupied[random.nextInt(6)] = true;
        }

        for (int i = 0; i < 6; i++) {
            if (occupied[i]) {
                state = state.with(ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i), true);
            }
        }

        this.addBlock(world, state, x, y, z, bb);

        BlockPos worldPos = this.offsetPos(x, y, z).toImmutable();
        if (!bb.contains(worldPos)) return;
        if (!(world.getBlockEntity(worldPos) instanceof ChiseledBookshelfBlockEntity shelf)) return;

        for (int slot = 0; slot < 6; slot++) {
            if (occupied[slot]) {
                ((ChiseledBookshelfBlockEntityAccessor) shelf).getHeldStacks().set(slot, createEnchantedBook(world, random));
            }
        }
    }

    @Unique
    private static ItemStack createEnchantedBook(StructureWorldAccess world, Random random) {
        RegistryKey<Enchantment> key = BOOK_POOL.get(random.nextInt(BOOK_POOL.size()));
        var registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        var holder = registry.getOrThrow(key);

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.add(holder, 1);
        book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
        return book;
    }
}
