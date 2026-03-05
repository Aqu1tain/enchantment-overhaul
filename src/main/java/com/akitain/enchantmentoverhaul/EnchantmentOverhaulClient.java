package com.akitain.enchantmentoverhaul;

import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EnchantmentOverhaulClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (SlotSystem.getBaseMaxSlots(stack) <= 0) return;
            lines.add(Text.literal(slotText(stack)).formatted(slotColor(stack)));
        });
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
