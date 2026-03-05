package com.akitain.enchantmentoverhaul.component;

import com.mojang.serialization.Codec;
import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {

    public static final ComponentType<Integer> GRINDSTONE_PENALTY = intComponent("grindstone_penalty");
    public static final ComponentType<Integer> HONING_LEVEL = intComponent("honing_level");
    public static final ComponentType<Integer> WARDING_LEVEL = intComponent("warding_level");
    public static final ComponentType<Integer> TEMPERING_LEVEL = intComponent("tempering_level");
    public static final ComponentType<Integer> GRINDING_LEVEL = intComponent("grinding_level");

    private static ComponentType<Integer> intComponent(String name) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(EnchantmentOverhaul.MOD_ID, name),
                ComponentType.<Integer>builder()
                        .codec(Codec.INT)
                        .packetCodec(PacketCodecs.VAR_INT)
                        .build()
        );
    }

    public static void register() {
    }
}
