package com.akitain.enchantmentoverhaul.mixin;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolMaterials.class)
public class ToolMaterialsMixin {

    @Inject(method = "getRepairIngredient", at = @At("HEAD"), cancellable = true)
    private void repairNetheriteWithDiamond(CallbackInfoReturnable<Ingredient> cir) {
        if ((Object) this == ToolMaterials.NETHERITE) {
            cir.setReturnValue(Ingredient.ofItems(Items.NETHERITE_INGOT, Items.DIAMOND));
        }
    }
}
