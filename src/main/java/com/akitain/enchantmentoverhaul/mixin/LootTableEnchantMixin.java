package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.loot.LootEnchanter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootTable.class)
public class LootTableEnchantMixin {

    @Redirect(
            method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootPool;addRandomItems(Ljava/util/function/Consumer;Lnet/minecraft/world/level/storage/loot/LootContext;)V")
    )
    private void enchantGeneratedEquipment(LootPool pool, Consumer<ItemStack> consumer, LootContext context) {
        pool.addRandomItems(stack -> {
            LootEnchanter.tryEnchant(stack, context);
            consumer.accept(stack);
        }, context);
    }
}
