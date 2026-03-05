package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

public record CatalogueData(List<Identifier> unlocked, int normalBookshelves) {

    public static final PacketCodec<RegistryByteBuf, CatalogueData> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()),
            CatalogueData::unlocked,
            PacketCodecs.VAR_INT,
            CatalogueData::normalBookshelves,
            CatalogueData::new
    );
}
