package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.DisabledEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {

    @Inject(method = "addMaxLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void levelOneBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        addFilteredBooks(entries, registryWrapper, stackVisibility);
        ci.cancel();
    }

    @Inject(method = "addAllLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void levelOneBooksSearch(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        addFilteredBooks(entries, registryWrapper, stackVisibility);
        ci.cancel();
    }

    private static void addFilteredBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility visibility) {
        registryWrapper.streamEntries()
                .filter(entry -> !DisabledEnchantments.isDisabled(entry))
                .map(entry -> EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(entry, 1)))
                .forEach(stack -> entries.add(stack, visibility));
    }
}
