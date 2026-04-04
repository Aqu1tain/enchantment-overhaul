package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {

    @Inject(method = "addMaxLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void levelOneBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> targets, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        addFilteredBooks(entries, stackVisibility);
        ci.cancel();
    }

    @Inject(method = "addAllLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void levelOneBooksSearch(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> targets, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        addFilteredBooks(entries, stackVisibility);
        ci.cancel();
    }

    private static void addFilteredBooks(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (DisabledEnchantments.isDisabled(enchantment)) continue;
            entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, 1)), visibility);
        }
    }
}
