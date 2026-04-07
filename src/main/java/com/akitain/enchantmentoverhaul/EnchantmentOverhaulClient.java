package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.client.CatalogueScreen;
import com.akitain.enchantmentoverhaul.client.ChiseledBookshelfHoverState;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EnchantmentOverhaulClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CATALOGUE, CatalogueScreen::new);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            addUpgradeLines(stack, lines);
            addInnatePropertyLine(stack, lines);
        });

        ClientTickEvents.END_CLIENT_TICK.register(ChiseledBookshelfHoverState::tick);
    }

    private static final String[] ROMAN = {"", "I", "II", "III", "IV", "V"};

    private static void addUpgradeLines(ItemStack stack, java.util.List<Text> lines) {
        for (UpgradeType type : UpgradeType.values()) {
            int level = type.currentLevel(stack);
            if (level <= 0) continue;
            String name = type.name().charAt(0) + type.name().substring(1).toLowerCase();
            String roman = level >= 1 && level <= 5 ? ROMAN[level] : String.valueOf(level);
            lines.add(Text.literal(name + " " + roman).formatted(Formatting.BLUE));
        }
    }

    private static void addInnatePropertyLine(ItemStack stack, java.util.List<Text> lines) {
        if (!stack.isIn(net.minecraft.registry.tag.ItemTags.ARMOR_ENCHANTABLE)) return;
        String material = InnateMaterialProperties.getMaterial(stack);
        if (material == null) return;
        String name = InnateMaterialProperties.getResistanceName(material);
        if (name == null) return;
        lines.add(Text.literal(name + " (5% per piece)").formatted(Formatting.DARK_AQUA));
    }

}
