package com.akitain.enchantmentoverhaul.mixin.client;

import com.akitain.enchantmentoverhaul.client.ChiseledBookshelfHoverState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class ChiseledBookshelfHoverMixin {

    @Shadow
    public abstract Font getFont();

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderChiseledBookshelfHover(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Component label = ChiseledBookshelfHoverState.label();
        if (label == null) return;

        int width = ChiseledBookshelfHoverState.width();
        int x = (graphics.guiWidth() - width) / 2;
        int y = graphics.guiHeight() - 73;
        graphics.textWithBackdrop(this.getFont(), label, x, y, width, ARGB.white(255));
    }
}
