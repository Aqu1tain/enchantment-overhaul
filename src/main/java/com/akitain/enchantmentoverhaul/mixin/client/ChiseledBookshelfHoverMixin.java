package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.client.ChiseledBookshelfHoverState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ChiseledBookshelfHoverMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void renderChiseledBookshelfHover(DrawContext context, float tickDelta, CallbackInfo ci) {
        Text label = ChiseledBookshelfHoverState.label();
        if (label == null) return;

        int width = ChiseledBookshelfHoverState.width();
        TextRenderer font = this.getTextRenderer();
        int x = (context.getScaledWindowWidth() - width) / 2;
        int y = context.getScaledWindowHeight() - 73;

        int bg = this.client.options.getTextBackgroundColor(0);
        if ((bg >>> 24) != 0) {
            context.fill(x - 2, y - 2, x + width + 2, y + font.fontHeight + 2, bg);
        }
        context.drawTextWithShadow(font, label, x, y, 0xFFFFFFFF);
    }
}
