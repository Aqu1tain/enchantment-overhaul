package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.UUID;

public enum UpgradeType {
    HONING(ModComponents.HONING_LEVEL),
    WARDING(ModComponents.WARDING_LEVEL),
    TEMPERING(ModComponents.TEMPERING_LEVEL),
    GRINDING(ModComponents.GRINDING_LEVEL);

    private static final UUID HONING_UUID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");
    private static final UUID GRINDING_UUID = UUID.fromString("b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e");

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
                    || item instanceof CrossbowItem || item instanceof TridentItem;
            case WARDING -> item instanceof ArmorItem;
            case TEMPERING -> stack.isDamageable();
            case GRINDING -> item instanceof MiningToolItem;
        };
    }

    public int currentLevel(ItemStack stack) {
        return ModComponents.getInt(stack, nbtKey, 0);
    }

    public void applyTo(ItemStack stack, int level) {
        int oldLevel = currentLevel(stack);
        ModComponents.setInt(stack, nbtKey, level);

        switch (this) {
            case HONING -> boostAttackDamage(stack, level - oldLevel);
            case GRINDING -> {} // applied via mixin on getBlockBreakingSpeed
            case WARDING, TEMPERING -> {}
        }
    }

    private void boostAttackDamage(ItemStack stack, double bonus) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList modifiers = nbt.contains("AttributeModifiers", NbtElement.LIST_TYPE)
                ? nbt.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE)
                : new NbtList();

        boolean found = false;
        for (int i = 0; i < modifiers.size(); i++) {
            NbtCompound mod = modifiers.getCompound(i);
            if (isUuid(mod, HONING_UUID)) {
                mod.putDouble("Amount", mod.getDouble("Amount") + bonus);
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
