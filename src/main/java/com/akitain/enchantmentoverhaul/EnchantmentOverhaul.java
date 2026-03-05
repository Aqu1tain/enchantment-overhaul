package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentOverhaul implements ModInitializer {

    public static final String MOD_ID = "enchantment-overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModScreenHandlers.register();
        LOGGER.info("Enchantment Overhaul loaded");
    }
}
