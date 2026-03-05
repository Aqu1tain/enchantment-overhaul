package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static final ExtendedScreenHandlerType<CatalogueScreenHandler, CatalogueData> CATALOGUE =
            new ExtendedScreenHandlerType<>(CatalogueScreenHandler::fromData, CatalogueData.PACKET_CODEC);

    public static void register() {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EnchantmentOverhaul.MOD_ID, "catalogue"), CATALOGUE);
    }
}
