package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentCosts {

    private static final Map<Enchantment, Item> REAGENTS = new HashMap<>();

    static {
        REAGENTS.put(Enchantments.FIRE_ASPECT, Items.BLAZE_POWDER);
        REAGENTS.put(Enchantments.FLAME, Items.BLAZE_POWDER);
        REAGENTS.put(Enchantments.CHANNELING, Items.LIGHTNING_ROD);
        REAGENTS.put(Enchantments.FROST_WALKER, Items.PACKED_ICE);
        REAGENTS.put(Enchantments.THORNS, Items.CACTUS);
        REAGENTS.put(Enchantments.FORTUNE, Items.EMERALD);
        REAGENTS.put(Enchantments.LOOTING, Items.RABBIT_FOOT);
        REAGENTS.put(Enchantments.SILK_TOUCH, Items.COBWEB);
        REAGENTS.put(Enchantments.LUCK_OF_THE_SEA, Items.NAUTILUS_SHELL);
        REAGENTS.put(Enchantments.INFINITY, Items.SPECTRAL_ARROW);
        REAGENTS.put(Enchantments.DEPTH_STRIDER, Items.PRISMARINE_SHARD);
        REAGENTS.put(Enchantments.SOUL_SPEED, Items.SOUL_SAND);
        REAGENTS.put(Enchantments.SWIFT_SNEAK, Items.ECHO_SHARD);
        REAGENTS.put(Enchantments.RIPTIDE, Items.HEART_OF_THE_SEA);
        REAGENTS.put(Enchantments.LOYALTY, Items.CHAIN);
        REAGENTS.put(Enchantments.MULTISHOT, Items.FIREWORK_ROCKET);
        REAGENTS.put(Enchantments.PIERCING, Items.ARROW);
        REAGENTS.put(Enchantments.RESPIRATION, Items.PUFFERFISH);
        REAGENTS.put(Enchantments.AQUA_AFFINITY, Items.PRISMARINE_CRYSTALS);
        REAGENTS.put(Enchantments.SWEEPING, Items.IRON_NUGGET);
        REAGENTS.put(Enchantments.KNOCKBACK, Items.PISTON);
        REAGENTS.put(Enchantments.PUNCH, Items.SNOWBALL);
        REAGENTS.put(Enchantments.FEATHER_FALLING, Items.FEATHER);
        REAGENTS.put(Enchantments.QUICK_CHARGE, Items.STRING);
        REAGENTS.put(Enchantments.LURE, Items.TROPICAL_FISH);
        REAGENTS.put(Enchantments.MENDING, Items.LAPIS_LAZULI);
        REAGENTS.put(Enchantments.BINDING_CURSE, Items.CHAIN);
        REAGENTS.put(Enchantments.VANISHING_CURSE, Items.PHANTOM_MEMBRANE);
        REAGENTS.put(ModEnchantments.STEP_UP, Items.RABBIT_FOOT);
        REAGENTS.put(ModEnchantments.VENOM, Items.SPIDER_EYE);
        REAGENTS.put(ModEnchantments.LAST_STAND, Items.GOLDEN_APPLE);
        REAGENTS.put(ModEnchantments.CURSE_OF_FRAGILITY, Items.GLASS_PANE);
        REAGENTS.put(ModEnchantments.CURSE_OF_HUNGER, Items.ROTTEN_FLESH);
        REAGENTS.put(ModEnchantments.VEIL, Items.FERMENTED_SPIDER_EYE);
    }

    private static final int[] XP_BY_LEVEL = {0, 2, 4, 7, 10};

    public static Item reagent(Enchantment enchantment) {
        return REAGENTS.getOrDefault(enchantment, Items.LAPIS_LAZULI);
    }

    public static int baseReagentCost(int level) {
        return level * 2;
    }

    public static int reagentCost(int level, int normalBookshelves) {
        double discount = BookshelfScanner.reagentDiscount(normalBookshelves);
        return Math.max(1, (int) Math.ceil(baseReagentCost(level) * (1.0 - discount)));
    }

    public static int xpCost(Enchantment enchantment, int level) {
        if (enchantment == Enchantments.MENDING) return 8;
        if (level >= XP_BY_LEVEL.length) return XP_BY_LEVEL[XP_BY_LEVEL.length - 1];
        return XP_BY_LEVEL[level];
    }

    public static boolean isReagent(Item item) {
        return REAGENTS.containsValue(item);
    }

    public static int slotCost(Enchantment enchantment, int level) {
        if (enchantment.isCursed()) return 0;
        if (enchantment == Enchantments.MENDING) return 3;
        return level;
    }
}
