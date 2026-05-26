package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;

public class InnateMaterialProperties {

    private static final float MAX_REDUCTION = 0.20f;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static float getDamageMultiplier(LivingEntity entity, DamageSource source) {
        int pieces = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack.isEmpty()) continue;
            String material = getMaterial(stack);
            if (material != null && resists(material, source)) pieces++;
        }
        if (pieces == 0) return 1.0f;
        return 1.0f - (MAX_REDUCTION * pieces / 4.0f);
    }

    public static String getMaterial(ItemStack stack) {
        var id = Registries.ITEM.getId(stack.getItem());
        String path = id.getPath();
        if ("additionaladditions".equals(id.getNamespace())) {
            if (path.startsWith("rose_gold_")) return "rose_gold";
            if (path.startsWith("gilded_netherite_")) return "netherite";
        }
        if (path.startsWith("netherite_")) return "netherite";
        if (path.startsWith("diamond_")) return "diamond";
        if (path.startsWith("golden_")) return "gold";
        if (path.startsWith("iron_")) return "iron";
        if (path.startsWith("chainmail_")) return "iron";
        if (path.startsWith("copper_")) return "copper";
        if (path.startsWith("leather_")) return "leather";
        return null;
    }

    public static boolean resists(String material, DamageSource source) {
        return switch (material) {
            case "netherite" -> source.isIn(DamageTypeTags.IS_FIRE);
            case "copper" -> isPoisonOrEffect(source);
            case "iron" -> source.isIn(DamageTypeTags.IS_PROJECTILE);
            case "diamond" -> source.isIn(DamageTypeTags.IS_EXPLOSION);
            case "rose_gold" -> source.isIn(DamageTypeTags.IS_EXPLOSION);
            case "gold" -> isMagicDamage(source);
            case "leather" -> source.isOf(DamageTypes.FALL);
            default -> false;
        };
    }

    private static boolean isMagicDamage(DamageSource source) {
        return source.isOf(DamageTypes.MAGIC)
                || source.isOf(DamageTypes.INDIRECT_MAGIC)
                || source.isOf(DamageTypes.WITHER)
                || source.isOf(DamageTypes.DRAGON_BREATH);
    }

    private static boolean isPoisonOrEffect(DamageSource source) {
        return source.isOf(DamageTypes.MAGIC)
                || source.isOf(DamageTypes.INDIRECT_MAGIC);
    }

    public static String getResistanceName(String material) {
        return switch (material) {
            case "netherite" -> "Fire Resistance";
            case "copper" -> "Poison Resistance";
            case "iron" -> "Projectile Resistance";
            case "diamond" -> "Explosion Resistance";
            case "rose_gold" -> "Explosion Resistance";
            case "gold" -> "Magic Resistance";
            case "leather" -> "Fall Resistance";
            default -> null;
        };
    }
}
