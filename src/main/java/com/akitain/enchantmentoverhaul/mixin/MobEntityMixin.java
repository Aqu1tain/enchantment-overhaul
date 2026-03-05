package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "updateEnchantments", at = @At("HEAD"), cancellable = true)
    protected void disableMobEnchantments(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        ci.cancel();
    }
}
