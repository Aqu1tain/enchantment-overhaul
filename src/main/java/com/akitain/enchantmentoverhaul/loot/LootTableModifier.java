package com.akitain.enchantmentoverhaul.loot;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class LootTableModifier {

    private record StructureLoot(String lootTable, int emptyWeight, int bookWeight, List<RegistryKey<Enchantment>> enchantments) {}

    private static final List<StructureLoot> STRUCTURE_LOOT = List.of(
            // Easy (15% chance)
            new StructureLoot("chests/village/village_temple", 85, 5, List.of(
                    Enchantments.FEATHER_FALLING, ModEnchantments.STEP_UP, Enchantments.KNOCKBACK)),
            new StructureLoot("chests/igloo_chest", 85, 15, List.of(
                    Enchantments.FROST_WALKER)),
            new StructureLoot("chests/shipwreck_treasure", 85, 5, List.of(
                    Enchantments.LUCK_OF_THE_SEA, ModEnchantments.BUOYANCY, Enchantments.LURE)),

            // Medium (25% chance)
            new StructureLoot("chests/desert_pyramid", 75, 12, List.of(
                    Enchantments.CHANNELING, ModEnchantments.CURSE_OF_FRAGILITY)),
            new StructureLoot("chests/jungle_temple", 75, 12, List.of(
                    Enchantments.THORNS, Enchantments.VANISHING_CURSE)),
            new StructureLoot("chests/underwater_ruin_big", 75, 8, List.of(
                    Enchantments.DEPTH_STRIDER, Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION)),
            new StructureLoot("chests/abandoned_mineshaft", 75, 12, List.of(
                    Enchantments.SILK_TOUCH, ModEnchantments.MAGNETIC)),
            new StructureLoot("chests/pillager_outpost", 75, 6, List.of(
                    Enchantments.MULTISHOT, Enchantments.PIERCING, Enchantments.QUICK_CHARGE, ModEnchantments.HOMING)),

            // Hard (35% chance)
            new StructureLoot("chests/buried_treasure", 65, 17, List.of(
                    Enchantments.RIPTIDE, Enchantments.LOYALTY)),
            new StructureLoot("chests/nether_bridge", 65, 12, List.of(
                    Enchantments.FIRE_ASPECT, Enchantments.FLAME, ModEnchantments.VENOM)),
            new StructureLoot("chests/woodland_mansion", 65, 12, List.of(
                    Enchantments.SWEEPING_EDGE, Enchantments.LOOTING, ModEnchantments.CURSE_OF_HUNGER)),
            new StructureLoot("chests/bastion_treasure", 65, 35, List.of(
                    Enchantments.SOUL_SPEED)),
            new StructureLoot("chests/stronghold_library", 65, 17, List.of(
                    Enchantments.INFINITY, Enchantments.FORTUNE)),

            // Endgame (50% chance)
            new StructureLoot("chests/ancient_city", 50, 17, List.of(
                    ModEnchantments.VEIL, Enchantments.SWIFT_SNEAK, Enchantments.BINDING_CURSE)),
            new StructureLoot("chests/trial_chambers/reward_rare", 50, 12, List.of(
                    Enchantments.WIND_BURST, Enchantments.BREACH, Enchantments.LUNGE, ModEnchantments.LAST_STAND)),
            new StructureLoot("chests/end_city_treasure", 50, 50, List.of(
                    Enchantments.MENDING))
    );

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) return;

            String path = key.getValue().getPath();
            RegistryWrapper<Enchantment> enchantmentRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);

            for (StructureLoot loot : STRUCTURE_LOOT) {
                if (!path.equals(loot.lootTable)) continue;
                addBookPool(tableBuilder, enchantmentRegistry, loot);
                break;
            }
        });
    }

    private static void addBookPool(LootTable.Builder tableBuilder, RegistryWrapper<Enchantment> registry, StructureLoot loot) {
        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1));

        pool.with(EmptyEntry.builder().weight(loot.emptyWeight));

        for (RegistryKey<Enchantment> key : loot.enchantments) {
            RegistryEntry<Enchantment> entry = registry.getOrThrow(key);
            pool.with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                    .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(entry, ConstantLootNumberProvider.create(1)))
                    .weight(loot.bookWeight));
        }

        tableBuilder.pool(pool);
    }
}
