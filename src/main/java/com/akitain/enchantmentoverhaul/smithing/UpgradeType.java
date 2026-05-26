package com.akitain.enchantmentoverhaul.smithing;

import com.google.common.collect.Multimap;
import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;

import java.util.Map;
import java.util.UUID;

public enum UpgradeType {
    HONING(ModComponents.HONING_LEVEL),
    WARDING(ModComponents.WARDING_LEVEL),
    TEMPERING(ModComponents.TEMPERING_LEVEL),
    GRINDING(ModComponents.GRINDING_LEVEL);

    private static final UUID HONING_UUID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");

    private final String nbtKey;

    UpgradeType(String nbtKey) {
        this.nbtKey = nbtKey;
    }

    public String nbtKey() {
        return nbtKey;
    }

    public boolean appliesTo(ItemStack stack) {
        Item item = stack.getItem();
        return switch (this) {
            case HONING -> item instanceof SwordItem || item instanceof BowItem
                    || item instanceof CrossbowItem || item instanceof TridentItem
                    || isAdditionalAdditionsWeapon(stack);
            case WARDING -> item instanceof ArmorItem || isAdditionalAdditionsArmor(stack);
            case TEMPERING -> stack.isDamageable();
            case GRINDING -> item instanceof MiningToolItem || isAdditionalAdditionsMiningTool(stack);
        };
    }

    private static boolean isAdditionalAdditionsWeapon(ItemStack stack) {
        var id = Registries.ITEM.getId(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_sword") || path.endsWith("_spear");
    }

    private static boolean isAdditionalAdditionsArmor(ItemStack stack) {
        var id = Registries.ITEM.getId(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_helmet")
                || path.endsWith("_chestplate")
                || path.endsWith("_leggings")
                || path.endsWith("_boots");
    }

    private static boolean isAdditionalAdditionsMiningTool(ItemStack stack) {
        var id = Registries.ITEM.getId(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_pickaxe")
                || path.endsWith("_axe")
                || path.endsWith("_shovel")
                || path.endsWith("_hoe");
    }

    public int currentLevel(ItemStack stack) {
        return ModComponents.getInt(stack, nbtKey, 0);
    }

    public void applyTo(ItemStack stack, int level) {
        ModComponents.setInt(stack, nbtKey, level);

        switch (this) {
            case HONING -> boostAttackDamage(stack, level);
            case GRINDING -> {} // applied via mixin on getBlockBreakingSpeed
            case WARDING, TEMPERING -> {}
        }
    }

    private void boostAttackDamage(ItemStack stack, int level) {
        double bonus = sharpnessBonus(level);
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList modifiers = nbt.contains("AttributeModifiers", NbtElement.LIST_TYPE)
                ? nbt.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE)
                : new NbtList();

        ensureDefaultMainhandModifiers(stack, modifiers);

        boolean found = false;
        for (int i = 0; i < modifiers.size(); i++) {
            NbtCompound mod = modifiers.getCompound(i);
            if (isUuid(mod, HONING_UUID)) {
                mod.putDouble("Amount", bonus);
                found = true;
                break;
            }
        }

        if (!found) {
            NbtCompound mod = new NbtCompound();
            mod.putString("AttributeName", "generic.attack_damage");
            mod.putString("Name", "Honing bonus");
            mod.putDouble("Amount", bonus);
            mod.putInt("Operation", 0);
            mod.putIntArray("UUID", uuidToIntArray(HONING_UUID));
            mod.putString("Slot", "mainhand");
            modifiers.add(mod);
        }

        nbt.put("AttributeModifiers", modifiers);
    }

    private static double sharpnessBonus(int level) {
        int cappedLevel = Math.min(level, 5);
        return cappedLevel <= 0 ? 0.0 : 1.0 + (cappedLevel - 1) * 0.5;
    }

    private static void ensureDefaultMainhandModifiers(ItemStack stack, NbtList modifiers) {
        Multimap<EntityAttribute, EntityAttributeModifier> defaultModifiers =
                stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND);

        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : defaultModifiers.entries()) {
            EntityAttributeModifier modifier = entry.getValue();
            if (containsUuid(modifiers, modifier.getId())) continue;

            NbtCompound mod = modifier.toNbt();
            mod.putString("AttributeName", Registries.ATTRIBUTE.getId(entry.getKey()).toString());
            mod.putString("Slot", "mainhand");
            modifiers.add(mod);
        }
    }

    private static boolean containsUuid(NbtList modifiers, UUID target) {
        for (int i = 0; i < modifiers.size(); i++) {
            if (isUuid(modifiers.getCompound(i), target)) return true;
        }
        return false;
    }

    private static boolean isUuid(NbtCompound mod, UUID target) {
        if (!mod.containsUuid("UUID")) return false;
        return mod.getUuid("UUID").equals(target);
    }

    private static int[] uuidToIntArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }
}
