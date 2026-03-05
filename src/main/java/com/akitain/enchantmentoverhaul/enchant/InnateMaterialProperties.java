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
        String id = Registries.ITEM.getId(stack.getItem()).getPath();
        if (id.startsWith("netherite_")) return "netherite";
        if (id.startsWith("diamond_")) return "diamond";
        if (id.startsWith("golden_")) return "gold";
        if (id.startsWith("iron_")) return "iron";
        if (id.startsWith("chainmail_")) return "iron";
        if (id.startsWith("copper_")) return "copper";
        if (id.startsWith("leather_")) return "leather";
        return null;
    }

    public static boolean resists(String material, DamageSource source) {
        return switch (material) {
            case "copper", "netherite" -> source.isIn(DamageTypeTags.IS_FIRE);
            case "iron" -> source.isIn(DamageTypeTags.IS_PROJECTILE);
            case "diamond" -> source.isIn(DamageTypeTags.IS_EXPLOSION);
            case "gold" -> isMagicDamage(source);
            default -> false;
        };
    }

    private static boolean isMagicDamage(DamageSource source) {
        return source.isOf(DamageTypes.MAGIC)
                || source.isOf(DamageTypes.INDIRECT_MAGIC)
                || source.isOf(DamageTypes.WITHER)
                || source.isOf(DamageTypes.DRAGON_BREATH);
    }

    public static String getResistanceName(String material) {
        return switch (material) {
            case "copper", "netherite" -> "Fire Resistance";
            case "iron" -> "Projectile Resistance";
            case "diamond" -> "Explosion Resistance";
            case "gold" -> "Magic Resistance";
            default -> null;
        };
    }
}
