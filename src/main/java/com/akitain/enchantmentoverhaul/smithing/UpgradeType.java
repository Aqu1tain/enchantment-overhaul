package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.entity.attribute.EntityAttribute;

public enum UpgradeType {
    HONING(ModComponents.HONING_LEVEL),
    WARDING(ModComponents.WARDING_LEVEL),
    TEMPERING(ModComponents.TEMPERING_LEVEL),
    GRINDING(ModComponents.GRINDING_LEVEL);

    private final ComponentType<Integer> component;

    UpgradeType(ComponentType<Integer> component) {
        this.component = component;
    }

    public ComponentType<Integer> component() {
        return component;
    }

    public boolean appliesTo(ItemStack stack) {
        return switch (this) {
            case HONING -> stack.isIn(ItemTags.WEAPON_ENCHANTABLE)
                    || stack.isIn(ItemTags.BOW_ENCHANTABLE)
                    || stack.isIn(ItemTags.CROSSBOW_ENCHANTABLE);
            case WARDING -> stack.isIn(ItemTags.ARMOR_ENCHANTABLE);
            case TEMPERING -> stack.isIn(ItemTags.DURABILITY_ENCHANTABLE);
            case GRINDING -> stack.isIn(ItemTags.MINING_ENCHANTABLE);
        };
    }

    public int currentLevel(ItemStack stack) {
        return stack.getOrDefault(component, 0);
    }

    public void applyTo(ItemStack stack, int level) {
        stack.set(component, level);

        switch (this) {
            case HONING -> applyAttribute(stack, EntityAttributes.ATTACK_DAMAGE, level, AttributeModifierSlot.MAINHAND);
            case WARDING -> applyAttribute(stack, EntityAttributes.ARMOR, level, AttributeModifierSlot.ARMOR);
            case GRINDING -> applyAttribute(stack, EntityAttributes.MINING_EFFICIENCY, level * 5, AttributeModifierSlot.MAINHAND);
            case TEMPERING -> {} // handled via mixin on item damage calculation
        }
    }

    private void applyAttribute(ItemStack stack, RegistryEntry<EntityAttribute> attribute, double value, AttributeModifierSlot slot) {
        Identifier id = Identifier.of(EnchantmentOverhaul.MOD_ID, name().toLowerCase());
        EntityAttributeModifier modifier = new EntityAttributeModifier(id, value, EntityAttributeModifier.Operation.ADD_VALUE);

        AttributeModifiersComponent existing = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        for (AttributeModifiersComponent.Entry entry : existing.modifiers()) {
            if (!entry.modifier().idMatches(id)) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot(), entry.display());
            }
        }
        builder.add(attribute, modifier, slot);
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }
}
