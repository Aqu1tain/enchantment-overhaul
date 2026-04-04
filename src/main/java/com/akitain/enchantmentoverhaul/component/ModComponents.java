package com.akitain.enchantmentoverhaul.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;

public class ModComponents {

    public static final DataComponentType<Integer> GRINDSTONE_PENALTY = intComponent("grindstone_penalty");
    public static final DataComponentType<Integer> HONING_LEVEL = intComponent("honing_level");
    public static final DataComponentType<Integer> WARDING_LEVEL = intComponent("warding_level");
    public static final DataComponentType<Integer> TEMPERING_LEVEL = intComponent("tempering_level");
    public static final DataComponentType<Integer> GRINDING_LEVEL = intComponent("grinding_level");

    private static DataComponentType<Integer> intComponent(String name) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, name),
                DataComponentType.<Integer>builder()
                        .persistent(Codec.INT)
                        .networkSynchronized(ByteBufCodecs.VAR_INT)
                        .build()
        );
    }

    public static void register() {
    }
}
