package com.akitain.enchantmentoverhaul.mixin.accessor;

import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChiseledBookshelfBlockEntity.class)
public interface ChiseledBookshelfBlockEntityAccessor {

    @Accessor("heldStacks")
    DefaultedList<ItemStack> getHeldStacks();
}
