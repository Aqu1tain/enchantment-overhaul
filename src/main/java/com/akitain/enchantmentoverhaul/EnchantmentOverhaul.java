package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.VillagerTrades;
import com.akitain.enchantmentoverhaul.enchant.MagneticHandler;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import com.akitain.enchantmentoverhaul.loot.LootTableModifier;
import com.akitain.enchantmentoverhaul.smithing.SmithingTemplates;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentOverhaul implements ModInitializer {

    public static final String MOD_ID = "enchantment-overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModScreenHandlers.register();
        SmithingTemplates.register();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(new ItemStack(SmithingTemplates.HONING_TEMPLATE));
            entries.add(new ItemStack(SmithingTemplates.WARDING_TEMPLATE));
            entries.add(new ItemStack(SmithingTemplates.TEMPERING_TEMPLATE));
            entries.add(new ItemStack(SmithingTemplates.GRINDING_TEMPLATE));
        });

        ServerTickEvents.END_WORLD_TICK.register(MagneticHandler::tick);
        LootTableModifier.register();
        VillagerTrades.register();

        LOGGER.info("Enchantment Overhaul loaded");
    }
}
