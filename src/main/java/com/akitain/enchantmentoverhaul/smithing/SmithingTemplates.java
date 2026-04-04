package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Map;

public class SmithingTemplates {

    private static final Formatting DESC = Formatting.BLUE;

    private static final Identifier SLOT_HELMET = Identifier.ofVanilla("container/slot/helmet");
    private static final Identifier SLOT_CHESTPLATE = Identifier.ofVanilla("container/slot/chestplate");
    private static final Identifier SLOT_LEGGINGS = Identifier.ofVanilla("container/slot/leggings");
    private static final Identifier SLOT_BOOTS = Identifier.ofVanilla("container/slot/boots");
    private static final Identifier SLOT_SWORD = Identifier.ofVanilla("container/slot/sword");
    private static final Identifier SLOT_PICKAXE = Identifier.ofVanilla("container/slot/pickaxe");
    private static final Identifier SLOT_AXE = Identifier.ofVanilla("container/slot/axe");
    private static final Identifier SLOT_SHOVEL = Identifier.ofVanilla("container/slot/shovel");
    private static final Identifier SLOT_HOE = Identifier.ofVanilla("container/slot/hoe");
    private static final Identifier SLOT_SPEAR = Identifier.ofVanilla("container/slot/spear");
    private static final Identifier SLOT_INGOT = Identifier.ofVanilla("container/slot/ingot");

    private static final List<Identifier> ARMOR_SLOTS = List.of(SLOT_HELMET, SLOT_CHESTPLATE, SLOT_LEGGINGS, SLOT_BOOTS);
    private static final List<Identifier> WEAPON_SLOTS = List.of(SLOT_SWORD, SLOT_AXE, SLOT_SPEAR);
    private static final List<Identifier> TOOL_SLOTS = List.of(SLOT_PICKAXE, SLOT_AXE, SLOT_SHOVEL, SLOT_HOE);
    private static final List<Identifier> ALL_EQUIPMENT_SLOTS = List.of(
            SLOT_HELMET, SLOT_CHESTPLATE, SLOT_LEGGINGS, SLOT_BOOTS,
            SLOT_SWORD, SLOT_PICKAXE, SLOT_AXE, SLOT_SHOVEL, SLOT_HOE, SLOT_SPEAR);
    private static final List<Identifier> MATERIAL_SLOTS = List.of(SLOT_INGOT);

    private static Text appliesTo(String key) {
        return Text.translatable("item." + EnchantmentOverhaul.MOD_ID + ".smithing_template." + key + ".applies_to").formatted(DESC);
    }

    private static Text ingredients(String key) {
        return Text.translatable("item." + EnchantmentOverhaul.MOD_ID + ".smithing_template." + key + ".ingredients").formatted(DESC);
    }

    private static Text baseSlot(String key) {
        return Text.translatable("item." + EnchantmentOverhaul.MOD_ID + ".smithing_template." + key + ".base_slot_description");
    }

    private static Text additionsSlot(String key) {
        return Text.translatable("item." + EnchantmentOverhaul.MOD_ID + ".smithing_template." + key + ".additions_slot_description");
    }

    public static final Item HONING_TEMPLATE = register("honing_template",
            appliesTo("honing"), ingredients("honing"), baseSlot("honing"), additionsSlot("honing"),
            WEAPON_SLOTS, MATERIAL_SLOTS);

    public static final Item WARDING_TEMPLATE = register("warding_template",
            appliesTo("warding"), ingredients("warding"), baseSlot("warding"), additionsSlot("warding"),
            ARMOR_SLOTS, MATERIAL_SLOTS);

    public static final Item TEMPERING_TEMPLATE = register("tempering_template",
            appliesTo("tempering"), ingredients("tempering"), baseSlot("tempering"), additionsSlot("tempering"),
            ALL_EQUIPMENT_SLOTS, MATERIAL_SLOTS);

    public static final Item GRINDING_TEMPLATE = register("grinding_template",
            appliesTo("grinding"), ingredients("grinding"), baseSlot("grinding"), additionsSlot("grinding"),
            TOOL_SLOTS, MATERIAL_SLOTS);

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

    private static Item register(String name, Text appliesTo, Text ingredients,
                                  Text baseSlotDesc, Text additionsSlotDesc,
                                  List<Identifier> baseSlotTextures, List<Identifier> additionsSlotTextures) {
        Identifier id = Identifier.of(EnchantmentOverhaul.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, key,
                new SmithingTemplateItem(appliesTo, ingredients, baseSlotDesc, additionsSlotDesc,
                        baseSlotTextures, additionsSlotTextures, new Item.Settings().registryKey(key).rarity(Rarity.UNCOMMON)));
    }

    public static void register() {}
}
