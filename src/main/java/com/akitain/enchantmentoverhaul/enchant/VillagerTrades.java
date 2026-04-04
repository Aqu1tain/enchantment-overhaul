package com.akitain.enchantmentoverhaul.enchant;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.List;

public class VillagerTrades {

    public static void register() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories ->
                factories.add(new BookFactory(List.of(Enchantments.FEATHER_FALLING, Enchantments.KNOCKBACK))));

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 1, factories ->
                factories.add(new BookFactory(List.of(Enchantments.FROST_WALKER, Enchantments.LUCK_OF_THE_SEA))));

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 1, factories ->
                factories.add(new BookFactory(List.of(ModEnchantments.STEP_UP))));
    }

    private static class BookFactory implements TradeOffers.Factory {
        private final List<Enchantment> pool;

        BookFactory(List<Enchantment> pool) {
            this.pool = pool;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            if (random.nextInt(2) == 0) return null;

            Enchantment enchantment = pool.get(random.nextInt(pool.size()));

            ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, 1));

            return new TradeOffer(
                    new ItemStack(Items.EMERALD, 10 + random.nextInt(21)),
                    book,
                    3, 10, 0.05f
            );
        }
    }
}
