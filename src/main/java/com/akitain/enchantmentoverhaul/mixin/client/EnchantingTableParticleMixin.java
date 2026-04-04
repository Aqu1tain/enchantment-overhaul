package com.akitain.enchantmentoverhaul.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableParticleMixin {

    @Inject(method = "randomDisplayTick", at = @At("TAIL"))
    private void addChiseledBookshelfParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (random.nextInt(16) != 0) continue;

            BlockPos shelfPos = pos.add(offset);
            BlockPos betweenPos = pos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2);

            if (!world.getBlockState(betweenPos).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) continue;

            BlockState shelfState = world.getBlockState(shelfPos);
            if (!shelfState.isOf(Blocks.CHISELED_BOOKSHELF)) continue;

            boolean hasBook = false;
            for (BooleanProperty prop : ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES) {
                if (shelfState.get(prop)) { hasBook = true; break; }
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
