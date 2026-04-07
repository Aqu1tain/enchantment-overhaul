package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.client.ChiseledBookshelfHoverState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ChiseledBookshelfHoverMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void renderChiseledBookshelfHover(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Text label = ChiseledBookshelfHoverState.label();
        if (label == null) return;

        int width = ChiseledBookshelfHoverState.width();
        int x = (context.getScaledWindowWidth() - width) / 2;
        int y = context.getScaledWindowHeight() - 73;
        context.drawTextWithBackground(this.getTextRenderer(), label, x, y, width, 0xFFFFFFFF);
    }
}
