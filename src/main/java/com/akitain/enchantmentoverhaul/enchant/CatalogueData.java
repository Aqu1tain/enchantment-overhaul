package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record CatalogueData(List<Identifier> unlocked, int normalBookshelves) {

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(unlocked.size());
        for (Identifier id : unlocked) buf.writeIdentifier(id);
        buf.writeVarInt(normalBookshelves);
    }

    public static CatalogueData read(PacketByteBuf buf) {
        int size = buf.readVarInt();
        List<Identifier> unlocked = new ArrayList<>(size);
        for (int i = 0; i < size; i++) unlocked.add(buf.readIdentifier());
        int bookshelves = buf.readVarInt();
        return new CatalogueData(unlocked, bookshelves);
    }
}
