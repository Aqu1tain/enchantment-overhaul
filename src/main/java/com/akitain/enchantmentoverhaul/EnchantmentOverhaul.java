package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import com.akitain.enchantmentoverhaul.enchant.VillagerTrades;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import com.akitain.enchantmentoverhaul.loot.LootTableModifier;
import com.akitain.enchantmentoverhaul.smithing.SmithingTemplates;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentOverhaul implements ModInitializer {

    public static final String MOD_ID = "enchantment-overhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModEnchantments.register();
        ModScreenHandlers.register();
        SmithingTemplates.register();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addBefore(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                    SmithingTemplates.HONING_TEMPLATE,
                    SmithingTemplates.WARDING_TEMPLATE,
                    SmithingTemplates.TEMPERING_TEMPLATE,
                    SmithingTemplates.GRINDING_TEMPLATE);
        });

        LootTableModifier.register();
        VillagerTrades.register();

        LOGGER.info("Enchantment Overhaul loaded");
    }
}
