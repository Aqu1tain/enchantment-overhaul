package com.akitain.enchantmentoverhaul.enchant;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

public class ModAdvancements {

    private static final Set<RegistryKey<Enchantment>> ENDGAME_ENCHANTMENTS = Set.of(
            ModEnchantments.VEIL, Enchantments.SWIFT_SNEAK, Enchantments.BINDING_CURSE,
            Enchantments.WIND_BURST, Enchantments.BREACH, Enchantments.LUNGE, ModEnchantments.LAST_STAND,
            Enchantments.MENDING
    );

    public static boolean isEndgame(RegistryKey<Enchantment> key) {
        return ENDGAME_ENCHANTMENTS.contains(key);
    }

    public static void checkEndgameBooks(ServerPlayerEntity player, Set<RegistryKey<Enchantment>> unlocked) {
        for (RegistryKey<Enchantment> key : unlocked) {
            if (isEndgame(key)) {
                grantForbiddenKnowledge(player);
                return;
            }
        }
    }

    public static void grantSmithingAdvancement(ServerPlayerEntity player, UpgradeType type) {
        grant(player, "hammer_time", "requirement");

        String criterion = switch (type) {
            case HONING -> "honing";
            case WARDING -> "warding";
            case TEMPERING -> "tempering";
            case GRINDING -> "grinding";
        };
        grant(player, "jack_of_all_trades", criterion);

        checkWalkingFortress(player);
    }

    public static void grantCurseAdvancement(ServerPlayerEntity player) {
        grant(player, "deal_with_the_devil", "requirement");
    }

    public static void grantForbiddenKnowledge(ServerPlayerEntity player) {
        grant(player, "forbidden_knowledge", "requirement");
    }

    public static void checkWalkingFortress(ServerPlayerEntity player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (player.getEquippedStack(slot).getOrDefault(ModComponents.WARDING_LEVEL, 0) < 5) return;
        }
        grant(player, "walking_fortress", "requirement");
    }

    private static void grant(ServerPlayerEntity player, String advancement, String criterion) {
        Identifier id = Identifier.of(EnchantmentOverhaul.MOD_ID, advancement);
        AdvancementEntry entry = player.getEntityWorld().getServer().getAdvancementLoader().get(id);
        if (entry != null) {
            player.getAdvancementTracker().grantCriterion(entry, criterion);
        }
    }
}
