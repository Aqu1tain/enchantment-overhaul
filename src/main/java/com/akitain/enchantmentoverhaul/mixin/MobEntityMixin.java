package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.gamerule.ModGameRules;
import com.akitain.enchantmentoverhaul.smithing.UpgradeType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "updateEnchantments", at = @At("HEAD"), cancellable = true)
    protected void overrideMobEnchantments(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        ci.cancel();

        if (!world.toServerWorld().getGameRules().getValue(ModGameRules.MOB_GEAR_ENCHANTMENTS)) return;

        MobEntity self = (MobEntity) (Object) this;
        List<EquipmentSlot> equipped = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (!self.getEquippedStack(slot).isEmpty()) equipped.add(slot);
        }
        if (equipped.isEmpty()) return;

        Difficulty difficulty = world.toServerWorld().getDifficulty();
        float regional = localDifficulty.getClampedLocalDifficulty();

        float enchantChance = switch (difficulty) {
            case PEACEFUL -> 0f;
            case EASY -> 0.10f + regional * 0.10f;
            case NORMAL -> 0.20f + regional * 0.15f;
            case HARD -> 0.35f + regional * 0.25f;
        };

        float upgradeChance = switch (difficulty) {
            case PEACEFUL -> 0f;
            case EASY -> 0.05f + regional * 0.07f;
            case NORMAL -> 0.12f + regional * 0.10f;
            case HARD -> 0.22f + regional * 0.18f;
        };

        if (random.nextFloat() < enchantChance) {
            EquipmentSlot slot = equipped.get(random.nextInt(equipped.size()));
            ItemStack stack = self.getEquippedStack(slot);
            EnchantmentHelper.applyEnchantmentProvider(stack, world.getRegistryManager(), EnchantmentProviders.MOB_SPAWN_EQUIPMENT, localDifficulty, random);
            self.equipStack(slot, stack);
        }

        if (random.nextFloat() < upgradeChance) {
            EquipmentSlot slot = equipped.get(random.nextInt(equipped.size()));
            ItemStack stack = self.getEquippedStack(slot);
            List<UpgradeType> applicable = new ArrayList<>();
            for (UpgradeType type : UpgradeType.values()) {
                if (type.appliesTo(stack)) applicable.add(type);
            }
            if (!applicable.isEmpty()) {
                UpgradeType upgrade = applicable.get(random.nextInt(applicable.size()));
                upgrade.applyTo(stack, upgrade.currentLevel(stack) + 1);
                self.equipStack(slot, stack);
            }
        }
    }
}
