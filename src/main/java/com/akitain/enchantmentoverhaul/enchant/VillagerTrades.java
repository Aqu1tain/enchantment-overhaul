package com.akitain.enchantmentoverhaul.enchant;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
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
        private final List<RegistryKey<Enchantment>> pool;

        BookFactory(List<RegistryKey<Enchantment>> pool) {
            this.pool = pool;
        }

        @Override
        public TradeOffer create(ServerWorld world, Entity entity, Random random) {
            if (random.nextInt(2) == 0) return null;

            RegistryKey<Enchantment> key = pool.get(random.nextInt(pool.size()));
            RegistryEntry<Enchantment> entry = world.getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOrThrow(key);

            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            builder.add(entry, 1);
            book.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

            return new TradeOffer(
                    new TradedItem(Items.EMERALD, 10 + random.nextInt(21)),
                    book,
                    3, 10, 0.05f
            );
        }
    }

}
