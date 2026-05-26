package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public enum UpgradeType {
    HONING(ModComponents.HONING_LEVEL),
    WARDING(ModComponents.WARDING_LEVEL),
    TEMPERING(ModComponents.TEMPERING_LEVEL),
    GRINDING(ModComponents.GRINDING_LEVEL);

    private final DataComponentType<Integer> component;

    UpgradeType(DataComponentType<Integer> component) {
        this.component = component;
    }

    public DataComponentType<Integer> component() {
        return component;
    }

    public boolean appliesTo(ItemStack stack) {
        return switch (this) {
            case HONING -> stack.is(ItemTags.WEAPON_ENCHANTABLE)
                    || stack.is(ItemTags.BOW_ENCHANTABLE)
                    || stack.is(ItemTags.CROSSBOW_ENCHANTABLE)
                    || isAdditionalAdditionsWeapon(stack);
            case WARDING -> stack.is(ItemTags.ARMOR_ENCHANTABLE) || isAdditionalAdditionsArmor(stack);
            case TEMPERING -> stack.is(ItemTags.DURABILITY_ENCHANTABLE);
            case GRINDING -> stack.is(ItemTags.MINING_ENCHANTABLE) || isAdditionalAdditionsMiningTool(stack);
        };
    }

    private static boolean isAdditionalAdditionsWeapon(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_sword") || path.endsWith("_spear");
    }

    private static boolean isAdditionalAdditionsArmor(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_helmet")
                || path.endsWith("_chestplate")
                || path.endsWith("_leggings")
                || path.endsWith("_boots");
    }

    private static boolean isAdditionalAdditionsMiningTool(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!"additionaladditions".equals(id.getNamespace())) return false;
        String path = id.getPath();
        return path.endsWith("_pickaxe")
                || path.endsWith("_axe")
                || path.endsWith("_shovel")
                || path.endsWith("_hoe");
    }

    public int currentLevel(ItemStack stack) {
        return stack.getOrDefault(component, 0);
    }

    public void applyTo(ItemStack stack, int level) {
        int oldLevel = currentLevel(stack);
        stack.set(component, level);

        switch (this) {
            case HONING -> boostBaseModifier(stack, Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID,
                    sharpnessBonus(level) - sharpnessBonus(oldLevel), EquipmentSlotGroup.MAINHAND);
            case WARDING -> {}
            case GRINDING -> replaceModifier(stack, Attributes.MINING_EFFICIENCY, efficiencyBonus(level), EquipmentSlotGroup.MAINHAND, "grinding");
            case TEMPERING -> {}
        }
    }

    private static double sharpnessBonus(int level) {
        int cappedLevel = Math.min(level, 5);
        return cappedLevel <= 0 ? 0.0 : 1.0 + (cappedLevel - 1) * 0.5;
    }

    private static double efficiencyBonus(int level) {
        int cappedLevel = Math.min(level, 5);
        return cappedLevel <= 0 ? 0.0 : cappedLevel * cappedLevel + 1.0;
    }

    private void boostBaseModifier(ItemStack stack, Holder<Attribute> attribute, Identifier baseId, double bonus, EquipmentSlotGroup slot) {
        if (bonus == 0.0) return;

        ItemAttributeModifiers existing = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        boolean found = false;
        for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
            if (entry.modifier().is(baseId)) {
                double newValue = entry.modifier().amount() + bonus;
                AttributeModifier boosted = new AttributeModifier(baseId, newValue, entry.modifier().operation());
                builder.add(entry.attribute(), boosted, entry.slot(), entry.display());
                found = true;
            } else {
                builder.add(entry.attribute(), entry.modifier(), entry.slot(), entry.display());
            }
        }

        if (!found) {
            builder.add(attribute, new AttributeModifier(baseId, bonus, AttributeModifier.Operation.ADD_VALUE), slot);
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private void replaceModifier(ItemStack stack, Holder<Attribute> attribute, double value, EquipmentSlotGroup slot, String modName) {
        Identifier modId = Identifier.withDefaultNamespace(modName);
        ItemAttributeModifiers existing = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
            if (!entry.modifier().is(modId)) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot(), entry.display());
            }
        }
        builder.add(attribute, new AttributeModifier(modId, value, AttributeModifier.Operation.ADD_VALUE), slot);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }
}
