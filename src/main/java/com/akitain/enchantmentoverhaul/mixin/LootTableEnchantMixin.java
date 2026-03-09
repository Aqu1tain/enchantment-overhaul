package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.loot.LootEnchanter;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableEnchantMixin {

    @Redirect(
            method = "generateUnprocessedLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootPool;addGeneratedLoot(Ljava/util/function/Consumer;Lnet/minecraft/loot/context/LootContext;)V")
    )
    private void enchantGeneratedEquipment(LootPool pool, Consumer<ItemStack> consumer, LootContext context) {
        pool.addGeneratedLoot(stack -> {
            LootEnchanter.tryEnchant(stack, context);
            consumer.accept(stack);
        }, context);
    }
}
