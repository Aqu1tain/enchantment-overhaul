package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PersistentProjectileEntity.class)
public abstract class HomingProjectileMixin extends ProjectileEntity {

    private HomingProjectileMixin() { super(null, null); }

    @Shadow public abstract ItemStack getWeaponStack();
    @Shadow protected abstract boolean isInGround();

    @Inject(method = "tick", at = @At("HEAD"))
    private void applyHoming(CallbackInfo ci) {
        PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;
        if (isInGround() || self.getEntityWorld().isClient()) return;

        ItemStack weapon = getWeaponStack();
        if (weapon == null || weapon.isEmpty()) return;

        ItemEnchantmentsComponent enchantments = weapon.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        boolean hasHoming = false;
        for (var entry : enchantments.getEnchantmentEntries()) {
            if (entry.getKey().matchesKey(ModEnchantments.HOMING)) { hasHoming = true; break; }
        }
        if (!hasHoming) return;

        Entity owner = this.getOwner();
        Box searchBox = self.getBoundingBox().expand(8.0);
        List<LivingEntity> targets = self.getEntityWorld().getEntitiesByClass(LivingEntity.class, searchBox,
                e -> e != owner && e.isAlive() && !e.isSpectator());
        if (targets.isEmpty()) return;

        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (LivingEntity target : targets) {
            double dist = self.squaredDistanceTo(target);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = target;
            }
        }

        Vec3d toTarget = nearest.getEyePos().subtract(self.getEntityPos()).normalize();
        Vec3d velocity = self.getVelocity();
        double speed = velocity.length();
        Vec3d blended = velocity.normalize().multiply(0.85).add(toTarget.multiply(0.15)).normalize().multiply(speed);
        self.setVelocity(blended);
        self.velocityDirty = true;
    }
}
