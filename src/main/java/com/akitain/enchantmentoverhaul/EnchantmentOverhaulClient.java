package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.client.CatalogueScreen;
import com.akitain.enchantmentoverhaul.enchant.ModScreenHandlers;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.fabricmc.api.ClientModInitializer;
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
            if (SlotSystem.getBaseMaxSlots(stack) <= 0) return;
            lines.add(Text.literal(slotText(stack)).formatted(slotColor(stack)));
        });
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

    private static String slotText(ItemStack stack) {
        int used = SlotSystem.getUsedSlots(stack);
        int max = SlotSystem.getMaxSlots(stack);
        int penalty = SlotSystem.getGrindstonePenalty(stack);
        int curseBonus = SlotSystem.getCurseBonus(stack);

        String text = "Slots: " + used + "/" + max;
        if (penalty > 0) text += " (-" + penalty + " grindstone)";
        if (curseBonus > 0) text += " (+" + curseBonus + " curse)";
        return text;
    }

    private static Formatting slotColor(ItemStack stack) {
        int used = SlotSystem.getUsedSlots(stack);
        int max = SlotSystem.getMaxSlots(stack);
        if (used >= max) return Formatting.RED;
        if (used > 0) return Formatting.YELLOW;
        return Formatting.GRAY;
    }
}
