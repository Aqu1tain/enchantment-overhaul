package com.music4music.enchantmentoverhaul;

import com.music4music.enchantmentoverhaul.enchant.SlotSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EnchantmentOverhaulClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            int max = SlotSystem.getBaseMaxSlots(stack);
            if (max <= 0) return;

            int effective = SlotSystem.getMaxSlots(stack);
            int used = SlotSystem.getUsedSlots(stack);
            int penalty = SlotSystem.getGrindstonePenalty(stack);
            int curseBonus = SlotSystem.getCurseBonus(stack);

            StringBuilder sb = new StringBuilder();
            sb.append("Slots: ").append(used).append("/").append(effective);

            if (penalty > 0) sb.append(" (-").append(penalty).append(" grindstone)");
            if (curseBonus > 0) sb.append(" (+").append(curseBonus).append(" curse)");

            Formatting color = used >= effective ? Formatting.RED
                    : used > 0 ? Formatting.YELLOW
                    : Formatting.GRAY;

            lines.add(Text.literal(sb.toString()).formatted(color));
        });
    }
}
