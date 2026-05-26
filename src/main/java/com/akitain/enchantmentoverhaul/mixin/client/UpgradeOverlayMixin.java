package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class UpgradeOverlayMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    private static final String MOD = "enchantment-overhaul";

    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @Inject(method = "renderArmor", at = @At("TAIL"))
    private void renderUpgradeOverlays(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot slot, int light, A model, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(slot);
        String material = getPaletteName(stack);
        if (material == null) return;

        boolean isLeggings = slot == EquipmentSlot.LEGS;
        String dir = isLeggings ? "trims/entity/humanoid_leggings" : "trims/entity/humanoid";

        if (ModComponents.getInt(stack, ModComponents.WARDING_LEVEL, 0) > 0) {
            renderOverlay(dir + "/warding_" + material, matrices, vertexConsumers, light, model);
        }

        if (ModComponents.getInt(stack, ModComponents.TEMPERING_LEVEL, 0) > 0) {
            renderOverlay(dir + "/tempering_" + material, matrices, vertexConsumers, light, model);
        }
    }

    @Unique
    private void renderOverlay(String spritePath, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model) {
        Sprite sprite = armorTrimsAtlas.getSprite(new Identifier(MOD, spritePath));
        VertexConsumer consumer = sprite.getTextureSpecificVertexConsumer(
                vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims()));
        model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Unique
    private static String getPaletteName(ItemStack stack) {
        String id = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();
        if (id.startsWith("chainmail_")) return "chainmail";

        String mat = InnateMaterialProperties.getMaterial(stack);
        if (mat == null) return null;
        return switch (mat) {
            case "copper" -> "copper";
            case "iron" -> "iron";
            case "gold" -> "gold";
            case "diamond" -> "diamond";
            case "rose_gold" -> "gold";
            case "netherite" -> "netherite";
            case "leather" -> "leather";
            default -> null;
        };
    }
}
