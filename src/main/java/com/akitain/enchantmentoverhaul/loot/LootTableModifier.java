package com.akitain.enchantmentoverhaul.loot;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import com.akitain.enchantmentoverhaul.smithing.SmithingTemplates;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class LootTableModifier {

    private record StructureLoot(String lootTable, int emptyWeight, int bookWeight, List<Enchantment> enchantments) {}

    private static final List<StructureLoot> STRUCTURE_LOOT = List.of(
            new StructureLoot("chests/village/village_temple", 85, 5, List.of(
                    Enchantments.FEATHER_FALLING, ModEnchantments.STEP_UP, Enchantments.KNOCKBACK)),
            new StructureLoot("chests/village/village_weaponsmith", 85, 5, List.of(
                    Enchantments.KNOCKBACK)),
            new StructureLoot("chests/village/village_armorer", 85, 5, List.of(
                    Enchantments.FEATHER_FALLING, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/village/village_fisher", 85, 5, List.of(
                    Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE)),
            new StructureLoot("chests/village/village_plains_house", 95, 2, List.of(
                    Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/village/village_desert_house", 95, 2, List.of(
                    Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/village/village_savanna_house", 95, 2, List.of(
                    Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/village/village_snowy_house", 95, 2, List.of(
                    Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/village/village_taiga_house", 95, 2, List.of(
                    Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK, ModEnchantments.STEP_UP)),
            new StructureLoot("chests/igloo_chest", 85, 15, List.of(
                    Enchantments.FROST_WALKER)),
            new StructureLoot("chests/shipwreck_treasure", 85, 5, List.of(
                    Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE)),
            new StructureLoot("chests/shipwreck_supply", 90, 5, List.of(
                    Enchantments.LUCK_OF_THE_SEA)),

            new StructureLoot("chests/simple_dungeon", 75, 12, List.of(
                    Enchantments.KNOCKBACK, Enchantments.THORNS, Enchantments.PUNCH)),
            new StructureLoot("chests/ruined_portal", 75, 12, List.of(
                    ModEnchantments.CURSE_OF_FRAGILITY, Enchantments.VANISHING_CURSE)),
            new StructureLoot("chests/underwater_ruin_small", 80, 8, List.of(
                    Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION)),
            new StructureLoot("chests/desert_pyramid", 75, 12, List.of(
                    Enchantments.CHANNELING, ModEnchantments.CURSE_OF_FRAGILITY, Enchantments.FIRE_ASPECT)),
            new StructureLoot("chests/jungle_temple", 75, 12, List.of(
                    Enchantments.THORNS, Enchantments.VANISHING_CURSE, Enchantments.LOOTING, ModEnchantments.CURSE_OF_HUNGER)),
            new StructureLoot("chests/underwater_ruin_big", 75, 8, List.of(
                    Enchantments.DEPTH_STRIDER, Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION, Enchantments.RIPTIDE, Enchantments.LOYALTY)),
            new StructureLoot("chests/abandoned_mineshaft", 75, 12, List.of(
                    Enchantments.SILK_TOUCH, Enchantments.FORTUNE)),
            new StructureLoot("chests/pillager_outpost", 75, 6, List.of(
                    Enchantments.MULTISHOT, Enchantments.PIERCING, Enchantments.QUICK_CHARGE, Enchantments.SWEEPING, Enchantments.PUNCH)),

            new StructureLoot("chests/buried_treasure", 65, 17, List.of(
                    Enchantments.RIPTIDE, Enchantments.LOYALTY, Enchantments.DEPTH_STRIDER)),
            new StructureLoot("chests/nether_bridge", 65, 12, List.of(
                    Enchantments.FIRE_ASPECT, Enchantments.FLAME, ModEnchantments.VENOM, Enchantments.LOOTING)),
            new StructureLoot("chests/woodland_mansion", 65, 12, List.of(
                    Enchantments.SWEEPING, Enchantments.LOOTING, ModEnchantments.CURSE_OF_HUNGER, Enchantments.SILK_TOUCH, Enchantments.THORNS)),
            new StructureLoot("chests/bastion_treasure", 65, 35, List.of(
                    Enchantments.SOUL_SPEED)),
            new StructureLoot("chests/bastion_bridge", 75, 12, List.of(
                    Enchantments.SOUL_SPEED)),
            new StructureLoot("chests/bastion_hoglin_stable", 75, 12, List.of(
                    Enchantments.SOUL_SPEED, Enchantments.FIRE_ASPECT)),
            new StructureLoot("chests/bastion_other", 80, 10, List.of(
                    Enchantments.SOUL_SPEED)),
            new StructureLoot("chests/stronghold_library", 65, 17, List.of(
                    Enchantments.INFINITY, Enchantments.FORTUNE)),
            new StructureLoot("chests/stronghold_corridor", 75, 12, List.of(
                    Enchantments.FORTUNE)),
            new StructureLoot("chests/stronghold_crossing", 75, 12, List.of(
                    Enchantments.INFINITY)),

            new StructureLoot("chests/ancient_city", 50, 17, List.of(
                    ModEnchantments.VEIL, Enchantments.SWIFT_SNEAK, Enchantments.BINDING_CURSE)),
            new StructureLoot("chests/end_city_treasure", 50, 50, List.of(
                    Enchantments.MENDING))
    );

    private record TemplateLoot(String lootTable, int emptyWeight, int templateWeight, List<Item> templates) {}

    private static final List<TemplateLoot> TEMPLATE_LOOT = List.of(
            new TemplateLoot("chests/village/village_weaponsmith", 80, 20, List.of(SmithingTemplates.HONING_TEMPLATE)),
            new TemplateLoot("chests/pillager_outpost", 75, 25, List.of(SmithingTemplates.HONING_TEMPLATE)),
            new TemplateLoot("chests/stronghold_library", 70, 30, List.of(SmithingTemplates.HONING_TEMPLATE)),
            new TemplateLoot("chests/simple_dungeon", 80, 20, List.of(SmithingTemplates.HONING_TEMPLATE)),
            new TemplateLoot("chests/village/village_armorer", 80, 20, List.of(SmithingTemplates.WARDING_TEMPLATE)),
            new TemplateLoot("chests/bastion_treasure", 70, 30, List.of(SmithingTemplates.WARDING_TEMPLATE)),
            new TemplateLoot("chests/buried_treasure", 75, 25, List.of(SmithingTemplates.WARDING_TEMPLATE)),
            new TemplateLoot("chests/jungle_temple", 75, 25, List.of(SmithingTemplates.WARDING_TEMPLATE)),
            new TemplateLoot("chests/village/village_toolsmith", 80, 10, List.of(SmithingTemplates.TEMPERING_TEMPLATE)),
            new TemplateLoot("chests/desert_pyramid", 75, 25, List.of(SmithingTemplates.TEMPERING_TEMPLATE)),
            new TemplateLoot("chests/shipwreck_treasure", 80, 20, List.of(SmithingTemplates.TEMPERING_TEMPLATE)),
            new TemplateLoot("chests/abandoned_mineshaft", 75, 12, List.of(SmithingTemplates.TEMPERING_TEMPLATE, SmithingTemplates.GRINDING_TEMPLATE)),
            new TemplateLoot("chests/simple_dungeon", 80, 20, List.of(SmithingTemplates.GRINDING_TEMPLATE)),
            new TemplateLoot("chests/desert_pyramid", 75, 25, List.of(SmithingTemplates.GRINDING_TEMPLATE)),
            new TemplateLoot("chests/ruined_portal", 75, 25, List.of(SmithingTemplates.GRINDING_TEMPLATE))
    );

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin()) return;

            String path = id.getPath();

            for (StructureLoot loot : STRUCTURE_LOOT) {
                if (!path.equals(loot.lootTable)) continue;
                addBookPool(tableBuilder, loot);
                break;
            }

            for (TemplateLoot loot : TEMPLATE_LOOT) {
                if (!path.equals(loot.lootTable)) continue;
                addTemplatePool(tableBuilder, loot);
                break;
            }
        });
    }

    private static void addBookPool(LootTable.Builder tableBuilder, StructureLoot loot) {
        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1));

        pool.with(EmptyEntry.builder().weight(loot.emptyWeight));

        for (Enchantment enchantment : loot.enchantments) {
            Identifier enchId = Registries.ENCHANTMENT.getId(enchantment);
            if (enchId == null) continue;

            NbtCompound bookNbt = new NbtCompound();
            NbtList storedEnchantments = new NbtList();
            NbtCompound enchNbt = new NbtCompound();
            enchNbt.putString("id", enchId.toString());
            enchNbt.putShort("lvl", (short) 1);
            storedEnchantments.add(enchNbt);
            bookNbt.put("StoredEnchantments", storedEnchantments);

            pool.with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(SetNbtLootFunction.builder(bookNbt))
                    .weight(loot.bookWeight));
        }

        tableBuilder.pool(pool);
    }

    private static void addTemplatePool(LootTable.Builder tableBuilder, TemplateLoot loot) {
        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1));

        pool.with(EmptyEntry.builder().weight(loot.emptyWeight));

        for (Item template : loot.templates) {
            pool.with(ItemEntry.builder(template).weight(loot.templateWeight));
        }

        tableBuilder.pool(pool);
    }
}
