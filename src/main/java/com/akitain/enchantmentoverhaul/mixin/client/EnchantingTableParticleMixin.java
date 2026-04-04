package com.akitain.enchantmentoverhaul.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableParticleMixin {

    @Inject(method = "animateTick", at = @At("TAIL"))
    private void addChiseledBookshelfParticles(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (random.nextInt(16) != 0) continue;

            BlockPos shelfPos = pos.offset(offset);
            BlockPos betweenPos = pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);

            if (!world.getBlockState(betweenPos).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) continue;

            BlockState shelfState = world.getBlockState(shelfPos);
            if (!shelfState.is(Blocks.CHISELED_BOOKSHELF)) continue;

            boolean hasBook = false;
            for (BooleanProperty prop : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES) {
                if (shelfState.getValue(prop)) { hasBook = true; break; }
            }
            if (!hasBook) continue;

            world.addParticle(
                    ParticleTypes.ENCHANT,
                    pos.getX() + 0.5,
                    pos.getY() + 2.0,
                    pos.getZ() + 0.5,
                    offset.getX() + random.nextFloat() - 0.5,
                    offset.getY() - random.nextFloat() - 1.0f,
                    offset.getZ() + random.nextFloat() - 0.5
            );
        }
    }
}
