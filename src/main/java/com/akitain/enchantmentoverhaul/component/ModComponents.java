package com.akitain.enchantmentoverhaul.component;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ModComponents {

    public static final String GRINDSTONE_PENALTY = "enchantment-overhaul:grindstone_penalty";
    public static final String HONING_LEVEL = "enchantment-overhaul:honing_level";
    public static final String WARDING_LEVEL = "enchantment-overhaul:warding_level";
    public static final String TEMPERING_LEVEL = "enchantment-overhaul:tempering_level";
    public static final String GRINDING_LEVEL = "enchantment-overhaul:grinding_level";

    public static int getInt(ItemStack stack, String key, int defaultValue) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(key)) return defaultValue;
        return nbt.getInt(key);
    }

    public static void setInt(ItemStack stack, String key, int value) {
        stack.getOrCreateNbt().putInt(key, value);
    }

    public static void register() {}
}
