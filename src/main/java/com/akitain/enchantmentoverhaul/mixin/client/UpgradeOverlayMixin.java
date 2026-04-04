package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.component.ModComponents;
import com.akitain.enchantmentoverhaul.enchant.InnateMaterialProperties;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class UpgradeOverlayMixin {

    private static final String MOD = "enchantment-overhaul";

    @Unique
    private SpriteAtlasTexture upgradeAtlas;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void captureAtlas(EquipmentModelLoader loader, SpriteAtlasTexture atlas, CallbackInfo ci) {
        this.upgradeAtlas = atlas;
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V", at = @At("TAIL"))
    private <S> void renderUpgradeOverlays(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey,
                                            Model<? super S> model, S state, ItemStack stack, MatrixStack matrices,
                                            OrderedRenderCommandQueue queue, int light, Identifier textureId,
                                            int outlineColor, int initialOrder, CallbackInfo ci) {
        if (upgradeAtlas == null) return;

        String material = getPaletteName(stack);
        if (material == null) return;

        String dir = layerType == EquipmentModel.LayerType.HUMANOID_LEGGINGS
                ? "trims/entity/humanoid_leggings" : "trims/entity/humanoid";

        int order = initialOrder;

        if (ModComponents.getInt(stack, ModComponents.WARDING_LEVEL, 0) > 0) {
            renderOverlay(dir + "/warding_" + material, model, state, matrices, queue, light, outlineColor, order++);
        }

        if (ModComponents.getInt(stack, ModComponents.TEMPERING_LEVEL, 0) > 0) {
            renderOverlay(dir + "/tempering_" + material, model, state, matrices, queue, light, outlineColor, order);
        }
    }

    @Unique
    private <S> void renderOverlay(String spritePath, Model<? super S> model, S state, MatrixStack matrices,
                                    OrderedRenderCommandQueue queue, int light, int outlineColor, int order) {
        Sprite sprite = upgradeAtlas.getSprite(Identifier.of(MOD, spritePath));
        if (sprite == null) return;

        queue.getBatchingQueue(order)
                .submitModel(model, state, matrices,
                        TexturedRenderLayers.getArmorTrims(false),
                        light, OverlayTexture.DEFAULT_UV, -1, sprite, outlineColor, null);
    }

    @Unique
    private static String getPaletteName(ItemStack stack) {
        String mat = InnateMaterialProperties.getMaterial(stack);
        if (mat == null) return null;
        return switch (mat) {
            case "copper" -> "copper";
            case "iron" -> "iron";
            case "gold" -> "gold";
            case "diamond" -> "diamond";
            case "netherite" -> "netherite";
            case "leather" -> "leather";
            default -> null;
        };
    }
}
