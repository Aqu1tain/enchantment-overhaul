package com.akitain.enchantmentoverhaul.enchant;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record CatalogueData(List<Identifier> unlocked, int normalBookshelves, boolean xpCostEnabled) {

    public static final StreamCodec<RegistryFriendlyByteBuf, CatalogueData> PACKET_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
            CatalogueData::unlocked,
            ByteBufCodecs.VAR_INT,
            CatalogueData::normalBookshelves,
            ByteBufCodecs.BOOL,
            CatalogueData::xpCostEnabled,
            CatalogueData::new
    );
}
