package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class InnateMaterialProperties {

    private static final float MAX_REDUCTION = 0.20f;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static float getDamageMultiplier(LivingEntity entity, DamageSource source) {
        int pieces = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            String material = getMaterial(stack);
            if (material != null && resists(material, source)) pieces++;
        }
        if (pieces == 0) return 1.0f;
        return 1.0f - (MAX_REDUCTION * pieces / 4.0f);
    }

    public static String getMaterial(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
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
            case "copper", "netherite" -> source.is(DamageTypeTags.IS_FIRE);
            case "iron" -> source.is(DamageTypeTags.IS_PROJECTILE);
            case "diamond" -> source.is(DamageTypeTags.IS_EXPLOSION);
            case "gold" -> isMagicDamage(source);
            default -> false;
        };
    }

    private static boolean isMagicDamage(DamageSource source) {
        return source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.DRAGON_BREATH);
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
