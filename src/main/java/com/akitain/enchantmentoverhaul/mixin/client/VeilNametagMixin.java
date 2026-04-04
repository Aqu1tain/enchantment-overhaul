package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class VeilNametagMixin {

    @Inject(method = "hasLabel(Lnet/minecraft/client/network/AbstractClientPlayerEntity;D)Z", at = @At("RETURN"), cancellable = true)
    private void hideVeilNametag(AbstractClientPlayerEntity entity, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        ItemStack helmet = entity.getEquippedStack(EquipmentSlot.HEAD);
        if (EnchantmentHelper.getLevel(ModEnchantments.VEIL, helmet) <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Entity camera = client.getCameraEntity();
        if (camera == null) return;

        Vec3d start = camera.getEyePos();
        Vec3d end = entity.getEyePos();
        BlockHitResult hit = entity.getWorld().raycast(new RaycastContext(
                start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, camera));
        if (hit.getType() == HitResult.Type.BLOCK) {
            cir.setReturnValue(false);
        }
    }
}
