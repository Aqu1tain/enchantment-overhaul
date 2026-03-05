package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SmithingTemplates {

    public static final Item HONING_TEMPLATE = register("honing_template");
    public static final Item WARDING_TEMPLATE = register("warding_template");
    public static final Item TEMPERING_TEMPLATE = register("tempering_template");
    public static final Item GRINDING_TEMPLATE = register("grinding_template");

    private static final Map<Item, UpgradeType> TEMPLATE_TO_TYPE = Map.of(
            HONING_TEMPLATE, UpgradeType.HONING,
            WARDING_TEMPLATE, UpgradeType.WARDING,
            TEMPERING_TEMPLATE, UpgradeType.TEMPERING,
            GRINDING_TEMPLATE, UpgradeType.GRINDING
    );

    private static final Map<Item, Integer> MATERIAL_TO_LEVEL = Map.of(
            Items.COPPER_INGOT, 1,
            Items.IRON_INGOT, 2,
            Items.GOLD_INGOT, 3,
            Items.DIAMOND, 4,
            Items.NETHERITE_INGOT, 5
    );

    private static final int[] XP_COSTS = {0, 1, 3, 5, 8, 12};

    public static UpgradeType getType(Item template) {
        return TEMPLATE_TO_TYPE.get(template);
    }

    public static int getMaterialLevel(Item material) {
        return MATERIAL_TO_LEVEL.getOrDefault(material, 0);
    }

    public static int getXpCost(int level) {
        if (level < 1 || level > 5) return 0;
        return XP_COSTS[level];
    }

    public static boolean isTemplate(Item item) {
        return TEMPLATE_TO_TYPE.containsKey(item);
    }

    private static Item register(String name) {
        Identifier id = Identifier.of(EnchantmentOverhaul.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, key, new Item(new Item.Settings().registryKey(key)));
    }

    public static void register() {}
}
