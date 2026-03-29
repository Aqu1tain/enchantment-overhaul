package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.Map;

import static java.util.Map.entry;

public class EnchantmentCosts {

    private static final Map<RegistryKey<Enchantment>, Item> REAGENTS = Map.ofEntries(
            entry(Enchantments.FIRE_ASPECT, Items.BLAZE_POWDER),
            entry(Enchantments.FLAME, Items.BLAZE_POWDER),
            entry(Enchantments.CHANNELING, Items.LIGHTNING_ROD),
            entry(Enchantments.FROST_WALKER, Items.PACKED_ICE),
            entry(Enchantments.THORNS, Items.CACTUS),
            entry(Enchantments.FORTUNE, Items.EMERALD),
            entry(Enchantments.LOOTING, Items.RABBIT_FOOT),
            entry(Enchantments.SILK_TOUCH, Items.COBWEB),
            entry(Enchantments.LUCK_OF_THE_SEA, Items.NAUTILUS_SHELL),
            entry(Enchantments.INFINITY, Items.SPECTRAL_ARROW),
            entry(Enchantments.DEPTH_STRIDER, Items.PRISMARINE_SHARD),
            entry(Enchantments.SOUL_SPEED, Items.SOUL_SAND),
            entry(Enchantments.SWIFT_SNEAK, Items.ECHO_SHARD),
            entry(Enchantments.RIPTIDE, Items.HEART_OF_THE_SEA),
            entry(Enchantments.LOYALTY, Items.IRON_CHAIN),
            entry(Enchantments.MULTISHOT, Items.FIREWORK_ROCKET),
            entry(Enchantments.PIERCING, Items.ARROW),
            entry(Enchantments.WIND_BURST, Items.BREEZE_ROD),
            entry(Enchantments.RESPIRATION, Items.PUFFERFISH),
            entry(Enchantments.AQUA_AFFINITY, Items.PRISMARINE_CRYSTALS),
            entry(Enchantments.SWEEPING_EDGE, Items.IRON_NUGGET),
            entry(Enchantments.BREACH, Items.BREEZE_ROD),
            entry(Enchantments.KNOCKBACK, Items.PISTON),
            entry(Enchantments.LUNGE, Items.SLIME_BALL),
            entry(Enchantments.FEATHER_FALLING, Items.FEATHER),
            entry(Enchantments.QUICK_CHARGE, Items.STRING),
            entry(Enchantments.LURE, Items.TROPICAL_FISH),
            entry(Enchantments.MENDING, Items.LAPIS_LAZULI),
            entry(Enchantments.BINDING_CURSE, Items.IRON_CHAIN),
            entry(Enchantments.VANISHING_CURSE, Items.PHANTOM_MEMBRANE),
            entry(ModEnchantments.STEP_UP, Items.RABBIT_FOOT),
            entry(ModEnchantments.VENOM, Items.SPIDER_EYE),
            entry(ModEnchantments.LAST_STAND, Items.GOLDEN_APPLE),
            entry(ModEnchantments.CURSE_OF_FRAGILITY, Items.GLASS_PANE),
            entry(ModEnchantments.CURSE_OF_HUNGER, Items.ROTTEN_FLESH),
            entry(ModEnchantments.VEIL, Items.FERMENTED_SPIDER_EYE)
    );

    private static final int[] XP_BY_LEVEL = {0, 2, 4, 7, 10};

    public static Item reagent(RegistryKey<Enchantment> key) {
        return REAGENTS.getOrDefault(key, Items.LAPIS_LAZULI);
    }

    public static int baseReagentCost(int level) {
        return level * 2;
    }

    public static int reagentCost(int level, int normalBookshelves) {
        double discount = BookshelfScanner.reagentDiscount(normalBookshelves);
        return Math.max(1, (int) Math.ceil(baseReagentCost(level) * (1.0 - discount)));
    }

    public static int xpCost(RegistryKey<Enchantment> key, int level) {
        if (key.equals(Enchantments.MENDING)) return 8;
        if (level >= XP_BY_LEVEL.length) return XP_BY_LEVEL[XP_BY_LEVEL.length - 1];
        return XP_BY_LEVEL[level];
    }

    public static boolean isReagent(Item item) {
        return REAGENTS.containsValue(item);
    }

    public static int slotCost(RegistryEntry<Enchantment> entry, int level) {
        if (entry.isIn(EnchantmentTags.CURSE)) return 0;
        if (entry.matchesKey(Enchantments.MENDING)) return 3;
        return level;
    }
}
