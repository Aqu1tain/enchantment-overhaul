package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.client.CatalogueScreen;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class EnchantmentOverhaulClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModScreenHandlers.CATALOGUE, CatalogueScreen::new);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            addUpgradeLines(stack, lines);
            addInnatePropertyLine(stack, lines);
        });
    }

    private static final String[] ROMAN = {"", "I", "II", "III", "IV", "V"};

    private static void addUpgradeLines(ItemStack stack, java.util.List<Component> lines) {
        for (UpgradeType type : UpgradeType.values()) {
            int level = type.currentLevel(stack);
            if (level <= 0) continue;
            String name = type.name().charAt(0) + type.name().substring(1).toLowerCase();
            String roman = level >= 1 && level <= 5 ? ROMAN[level] : String.valueOf(level);
            lines.add(Component.literal(name + " " + roman).withStyle(ChatFormatting.BLUE));
        }
    }

    private static void addInnatePropertyLine(ItemStack stack, java.util.List<Component> lines) {
        if (!stack.is(net.minecraft.tags.ItemTags.ARMOR_ENCHANTABLE)) return;
        String material = InnateMaterialProperties.getMaterial(stack);
        if (material == null) return;
        String name = InnateMaterialProperties.getResistanceName(material);
        if (name == null) return;
        lines.add(Component.literal(name + " (5% per piece)").withStyle(ChatFormatting.DARK_AQUA));
    }

}
