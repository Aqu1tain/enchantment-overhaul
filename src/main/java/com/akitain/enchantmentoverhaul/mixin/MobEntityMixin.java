package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobEntityMixin {

    @Inject(method = "populateDefaultEquipmentEnchantments", at = @At("HEAD"), cancellable = true)
    protected void disableMobEnchantments(ServerLevelAccessor world, RandomSource random, DifficultyInstance localDifficulty, CallbackInfo ci) {
        ci.cancel();
    }
}
