package com.akitain.enchantmentoverhaul.smithing;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

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
            case HONING -> boostBaseModifier(stack, EntityAttributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, level, AttributeModifierSlot.MAINHAND);
            case WARDING -> addModifier(stack, EntityAttributes.ARMOR, level, AttributeModifierSlot.ARMOR);
            case GRINDING -> addModifier(stack, EntityAttributes.MINING_EFFICIENCY, level * 5, AttributeModifierSlot.MAINHAND);
            case TEMPERING -> {}
        }
    }

    private void boostBaseModifier(ItemStack stack, RegistryEntry<EntityAttribute> attribute, Identifier baseId, double bonus, AttributeModifierSlot slot) {
        AttributeModifiersComponent existing = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        boolean found = false;
        for (AttributeModifiersComponent.Entry entry : existing.modifiers()) {
            if (entry.modifier().idMatches(baseId)) {
                double newValue = entry.modifier().value() + bonus;
                EntityAttributeModifier boosted = new EntityAttributeModifier(baseId, newValue, entry.modifier().operation());
                builder.add(entry.attribute(), boosted, entry.slot(), entry.display());
                found = true;
            } else {
                builder.add(entry.attribute(), entry.modifier(), entry.slot(), entry.display());
            }
        }

        if (!found) {
            builder.add(attribute, new EntityAttributeModifier(baseId, bonus, EntityAttributeModifier.Operation.ADD_VALUE), slot);
        }

        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private void addModifier(ItemStack stack, RegistryEntry<EntityAttribute> attribute, double value, AttributeModifierSlot slot) {
        AttributeModifiersComponent existing = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, existing.with(attribute,
                new EntityAttributeModifier(Identifier.ofVanilla(name().toLowerCase()), value, EntityAttributeModifier.Operation.ADD_VALUE), slot));
    }
}
