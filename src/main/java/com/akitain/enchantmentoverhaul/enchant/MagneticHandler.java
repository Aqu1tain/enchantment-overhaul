package com.akitain.enchantmentoverhaul.enchant;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MagneticHandler {

    private static final double[] RANGE_BY_LEVEL = {0, 3, 5, 8};

    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ItemStack mainhand = player.getMainHandStack();
            int level = getLevel(mainhand);
            if (level <= 0) continue;

            double range = level < RANGE_BY_LEVEL.length ? RANGE_BY_LEVEL[level] : RANGE_BY_LEVEL[RANGE_BY_LEVEL.length - 1];
            Box box = player.getBoundingBox().expand(range);

            for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class, box, e -> !e.cannotPickup())) {
                Vec3d dir = player.getEntityPos().add(0, 0.5, 0).subtract(item.getEntityPos());
                double dist = dir.length();
                if (dist < 1.0) continue;
                Vec3d velocity = dir.normalize().multiply(0.3);
                item.setVelocity(velocity);
                item.velocityDirty = true;
            }
        }
    }

    private static int getLevel(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.MAGNETIC)) return entry.getIntValue();
        }
        return 0;
    }
}
