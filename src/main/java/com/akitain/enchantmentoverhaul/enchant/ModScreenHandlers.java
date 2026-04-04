package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModScreenHandlers {

    public static final ExtendedMenuType<CatalogueScreenHandler, CatalogueData> CATALOGUE =
            new ExtendedMenuType<>(CatalogueScreenHandler::fromData, CatalogueData.PACKET_CODEC);

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(EnchantmentOverhaul.MOD_ID, "catalogue"), CATALOGUE);
    }
}
