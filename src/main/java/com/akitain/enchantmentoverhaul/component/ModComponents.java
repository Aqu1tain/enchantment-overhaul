package com.akitain.enchantmentoverhaul.component;

import com.mojang.serialization.Codec;
import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {

    public static final ComponentType<Integer> GRINDSTONE_PENALTY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(EnchantmentOverhaul.MOD_ID, "grindstone_penalty"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT)
                    .build()
    );

    public static void register() {
    }
}
