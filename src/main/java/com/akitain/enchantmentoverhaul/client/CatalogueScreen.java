package com.akitain.enchantmentoverhaul.client;

import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler;
import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler.CatalogueEntry;
import com.akitain.enchantmentoverhaul.enchant.EnchantmentCosts;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CatalogueScreen extends HandledScreen<CatalogueScreenHandler> {

    private static final int BG_W = 280;
    private static final int BG_H = 230;

    private static final int CAT_X = 52;
    private static final int CAT_Y = 20;
    private static final int CAT_W = 216;
    private static final int ROW_H = 20;
    private static final int VISIBLE_ROWS = 5;
    private static final int CAT_H = ROW_H * VISIBLE_ROWS;

    private static final int SCROLLBAR_W = 6;

    private static final int BTN_X = 8;
    private static final int BTN_Y = 108;
    private static final int BTN_W = 36;
    private static final int BTN_H = 14;

    private static final int SLOT_BAR_Y = 126;

    private static final int PURPLE_DARK = 0xFF1A0A2E;
    private static final int PURPLE_MID = 0xFF2D1250;
    private static final int PURPLE_LIGHT = 0xFF8050C0;
    private static final int PURPLE_BRIGHT = 0xFFC080FF;
    private static final int TEXT_LIGHT = 0xFFE0D8F0;
    private static final int TEXT_DIM = 0xFF9080B0;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int GOLD = 0xFFFFAA00;
    private static final int BG = 0xFFC6C6C6;
    private static final int SLOT_BG = 0xFF8B8B8B;
    private static final int BORDER_L = 0xFFFFFFFF;
    private static final int BORDER_D = 0xFF555555;

    private float scrollAmount;
    private int scrollOffset;
    private boolean scrolling;
    private ItemStack lastItem = ItemStack.EMPTY;

    public CatalogueScreen(CatalogueScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BG_W;
        this.backgroundHeight = BG_H;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 59;
        this.playerInventoryTitleY = BG_H - 94;
    }

    @Override
    protected void init() {
        super.init();
        handler.rebuildEntries();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        checkItemChanged();
        super.render(context, mouseX, mouseY, deltaTicks);
        drawCatalogueTooltip(context, mouseX, mouseY);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void checkItemChanged() {
        ItemStack current = handler.getSlot(0).getStack();
        if (!ItemStack.areEqual(current, lastItem)) {
            lastItem = current.copy();
            handler.rebuildEntries();
            scrollAmount = 0;
            scrollOffset = 0;
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = this.x, y = this.y;

        context.fill(x, y, x + BG_W, y + BG_H, BG);
        border3D(context, x, y, BG_W, BG_H, BORDER_L, BORDER_D);

        drawInputSlots(context, x, y);
        drawCatalogue(context, x, y, mouseX, mouseY);
        drawSlotBar(context, x, y);
        drawEnchantButton(context, x, y, mouseX, mouseY);
        drawPlayerSlotBorders(context, x, y);
    }

    private void drawInputSlots(DrawContext context, int x, int y) {
        for (int i = 0; i < 3; i++) {
            Slot slot = handler.slots.get(i);
            slotBorder(context, x + slot.x - 1, y + slot.y - 1);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, this.title, titleX, titleY, 0x404040, false);

        Slot s0 = handler.slots.get(0);
        Slot s1 = handler.slots.get(1);
        Slot s2 = handler.slots.get(2);
        context.drawText(textRenderer, "Item", s0.x - 1, s0.y + 18, TEXT_DIM, true);
        context.drawText(textRenderer, "Lapis", s1.x - 1, s1.y + 18, TEXT_DIM, true);
        context.drawText(textRenderer, "Reagent", s2.x - 4, s2.y + 18, TEXT_DIM, true);

        context.drawText(textRenderer, this.playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0x404040, false);
    }

    private void drawCatalogue(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int cx = x + CAT_X, cy = y + CAT_Y;

        context.fill(cx, cy, cx + CAT_W, cy + CAT_H, PURPLE_DARK);
        borderInset(context, cx, cy, CAT_W, CAT_H);

        context.drawTextWithShadow(textRenderer, "Catalogue", cx, cy - 12, TEXT_LIGHT);

        List<CatalogueEntry> entries = handler.getEntries();
        if (entries.isEmpty()) {
            String hint = handler.getSlot(0).getStack().isEmpty()
                    ? "Place an item to enchant"
                    : "No enchantments unlocked";
            context.drawTextWithShadow(textRenderer, hint,
                    cx + (CAT_W - textRenderer.getWidth(hint)) / 2,
                    cy + CAT_H / 2 - 4, TEXT_DIM);
            return;
        }

        int end = Math.min(scrollOffset + VISIBLE_ROWS, entries.size());
        for (int i = scrollOffset; i < end; i++) {
            int ry = cy + (i - scrollOffset) * ROW_H;
            drawRow(context, entries.get(i), i, cx, ry, mouseX, mouseY);
        }

        drawScrollbar(context, cx + CAT_W - SCROLLBAR_W - 1, cy + 1, CAT_H - 2);
    }

    private void drawRow(DrawContext context, CatalogueEntry entry, int idx, int cx, int ry, int mx, int my) {
        boolean selected = idx == handler.getSelectedIndex();
        boolean hovered = mx >= cx && mx < cx + CAT_W && my >= ry && my < ry + ROW_H;

        if (selected) {
            context.fill(cx + 1, ry, cx + CAT_W - SCROLLBAR_W - 2, ry + ROW_H, 0x50A060FF);
        } else if (hovered) {
            context.fill(cx + 1, ry, cx + CAT_W - SCROLLBAR_W - 2, ry + ROW_H, 0x30FFFFFF);
        }

        int ty = ry + (ROW_H - 8) / 2;
        boolean curse = entry.entry().isIn(EnchantmentTags.CURSE);
        int nameColor = curse ? 0xFFFF6666 : TEXT_LIGHT;
        String name = entry.entry().value().description().getString();
        context.drawTextWithShadow(textRenderer, name, cx + 6, ty, nameColor);

        int lvX = cx + CAT_W - SCROLLBAR_W - 8 - entry.maxLevel() * 14;
        int selLv = selected ? handler.getSelectedLevel() : 0;

        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            boolean lvSel = selected && lv == selLv;
            int bg = lvSel ? PURPLE_LIGHT : PURPLE_MID;
            context.fill(lvX, ry + 3, lvX + 12, ry + ROW_H - 3, bg);
            String r = toRoman(lv);
            int tw = textRenderer.getWidth(r);
            context.drawTextWithShadow(textRenderer, r, lvX + (12 - tw) / 2, ty, lvSel ? GOLD : TEXT_DIM);
            lvX += 14;
        }
    }

    private void drawCatalogueTooltip(DrawContext context, int mx, int my) {
        int cx = this.x + CAT_X, cy = this.y + CAT_Y;
        if (mx < cx || mx >= cx + CAT_W || my < cy || my >= cy + CAT_H) return;

        List<CatalogueEntry> entries = handler.getEntries();
        int idx = scrollOffset + (my - cy) / ROW_H;
        if (idx >= entries.size()) return;

        CatalogueEntry entry = entries.get(idx);
        RegistryKey<Enchantment> key = entry.key();
        boolean selected = idx == handler.getSelectedIndex();
        int level = selected ? handler.getSelectedLevel() : 1;

        Item reagentItem = EnchantmentCosts.reagent(key);
        int lapisCost = EnchantmentCosts.lapisCost(level);
        int reagentCost = EnchantmentCosts.reagentCost(level, handler.getNormalBookshelves());
        int xpCost = EnchantmentCosts.xpCost(key, level);
        int slotCost = EnchantmentCosts.slotCost(key, level);

        ItemStack item = handler.getSlot(0).getStack();
        ItemStack lapis = handler.getSlot(1).getStack();
        ItemStack reagent = handler.getSlot(2).getStack();
        int playerXp = MinecraftClient.getInstance().player.experienceLevel;

        boolean hasLapis = lapis.getCount() >= lapisCost;
        boolean hasReagent = reagent.isOf(reagentItem) && reagent.getCount() >= reagentCost;
        boolean hasXp = playerXp >= xpCost;
        boolean hasSlots = SlotSystem.getAvailableSlots(item) >= slotCost;

        String levelLabel = selected ? " " + toRoman(level) : "";
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.literal(entry.entry().value().description().getString() + levelLabel)
                .formatted(entry.entry().isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.LIGHT_PURPLE));
        tooltip.add(Text.empty());
        tooltip.add(costLine("Lapis Lazuli", lapisCost, hasLapis));
        tooltip.add(costLine(reagentItem.getName().getString(), reagentCost, hasReagent));
        tooltip.add(costLine("XP Levels", xpCost, hasXp));
        tooltip.add(costLine("Slots", slotCost, hasSlots));

        if (handler.getNormalBookshelves() > 0) {
            int pct = (int) (EnchantmentCosts.baseReagentCost(level) > 0
                    ? (1.0 - (double) reagentCost / EnchantmentCosts.baseReagentCost(level)) * 100 : 0);
            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Bookshelves: " + handler.getNormalBookshelves() + " (-" + pct + "% reagent)")
                    .formatted(Formatting.DARK_GRAY));
        }

        context.drawTooltip(textRenderer, tooltip, mx, my);
    }

    private Text costLine(String label, int amount, boolean has) {
        Formatting color = has ? Formatting.GREEN : Formatting.RED;
        return Text.literal(label + ": ").formatted(Formatting.GRAY)
                .append(Text.literal(String.valueOf(amount)).formatted(color));
    }

    private void drawScrollbar(DrawContext context, int sx, int sy, int sh) {
        context.fill(sx, sy, sx + SCROLLBAR_W, sy + sh, 0x40000000);
        if (!shouldScroll()) return;

        int thumbH = Math.max(10, sh * VISIBLE_ROWS / handler.getEntries().size());
        int thumbY = sy + (int) ((sh - thumbH) * scrollAmount);
        context.fill(sx, thumbY, sx + SCROLLBAR_W, thumbY + thumbH, PURPLE_LIGHT);
    }

    private void drawSlotBar(DrawContext context, int x, int y) {
        ItemStack item = handler.getSlot(0).getStack();
        if (item.isEmpty()) return;

        int max = SlotSystem.getMaxSlots(item);
        int used = SlotSystem.getUsedSlots(item);
        if (max <= 0) return;

        int pendingCost = 0;
        if (handler.getSelectedIndex() >= 0 && handler.getSelectedIndex() < handler.getEntries().size()) {
            CatalogueEntry e = handler.getEntries().get(handler.getSelectedIndex());
            pendingCost = EnchantmentCosts.slotCost(e.key(), handler.getSelectedLevel());
        }

        int barX = x + CAT_X;
        int barY = y + SLOT_BAR_Y;
        int lw = 12, gap = 2;
        int totalW = max * lw + (max - 1) * gap;
        int sx = barX + (CAT_W - totalW) / 2;

        for (int i = 0; i < max; i++) {
            int lx = sx + i * (lw + gap);
            if (i < used) {
                context.fill(lx, barY, lx + lw, barY + 8, PURPLE_LIGHT);
            } else if (i < used + pendingCost) {
                boolean blink = (System.currentTimeMillis() / 400) % 2 == 0;
                context.fill(lx, barY, lx + lw, barY + 8, blink ? PURPLE_BRIGHT : 0xFF5030A0);
            } else {
                context.fill(lx, barY, lx + lw, barY + 8, PURPLE_MID);
            }
        }

        context.drawTextWithShadow(textRenderer, used + "/" + max, sx + totalW + 6, barY, TEXT_LIGHT);
    }

    private void drawEnchantButton(DrawContext context, int x, int y, int mx, int my) {
        int bx = x + BTN_X, by = y + BTN_Y;
        boolean can = canEnchantNow();
        boolean hover = mx >= bx && mx < bx + BTN_W && my >= by && my < by + BTN_H;

        int bg = can ? (hover ? 0xFF50B050 : 0xFF408040) : 0xFF555555;
        context.fill(bx, by, bx + BTN_W, by + BTN_H, bg);
        border3D(context, bx, by, BTN_W, BTN_H,
                can ? 0xFF70D070 : 0xFF777777,
                can ? 0xFF206020 : 0xFF333333);

        String label = "Enchant";
        context.drawTextWithShadow(textRenderer, label,
                bx + (BTN_W - textRenderer.getWidth(label)) / 2,
                by + (BTN_H - 8) / 2,
                can ? 0xFFFFFFFF : 0xFF999999);
    }

    private void drawPlayerSlotBorders(DrawContext context, int x, int y) {
        for (int i = 3; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            slotBorder(context, x + slot.x - 1, y + slot.y - 1);
        }
    }

    private boolean canEnchantNow() {
        if (handler.getSelectedIndex() < 0) return false;
        List<CatalogueEntry> entries = handler.getEntries();
        if (handler.getSelectedIndex() >= entries.size()) return false;

        CatalogueEntry entry = entries.get(handler.getSelectedIndex());

        int level = handler.getSelectedLevel();
        RegistryKey<Enchantment> key = entry.key();
        ItemStack item = handler.getSlot(0).getStack();
        ItemStack lapis = handler.getSlot(1).getStack();
        ItemStack reagent = handler.getSlot(2).getStack();

        if (MinecraftClient.getInstance().player.isCreative()) return true;

        return lapis.getCount() >= EnchantmentCosts.lapisCost(level)
                && reagent.isOf(EnchantmentCosts.reagent(key))
                && reagent.getCount() >= EnchantmentCosts.reagentCost(level, handler.getNormalBookshelves())
                && MinecraftClient.getInstance().player.experienceLevel >= EnchantmentCosts.xpCost(key, level)
                && SlotSystem.getAvailableSlots(item) >= EnchantmentCosts.slotCost(key, level);
    }

    // --- Input ---

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int x = this.x, y = this.y;

        if (clickEnchant(mx, my, x, y)) return true;
        if (clickRow(mx, my, x, y)) return true;
        if (clickScroll(mx, my, x, y)) return true;

        return super.mouseClicked(click, doubled);
    }

    private boolean clickEnchant(double mx, double my, int x, int y) {
        int bx = x + BTN_X, by = y + BTN_Y;
        if (mx < bx || mx >= bx + BTN_W || my < by || my >= by + BTN_H) return false;
        if (!canEnchantNow()) return false;
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F));
        client.interactionManager.clickButton(handler.syncId, CatalogueScreenHandler.ENCHANT_BUTTON_ID);
        return true;
    }

    private boolean clickRow(double mx, double my, int x, int y) {
        int cx = x + CAT_X, cy = y + CAT_Y;
        if (mx < cx || mx >= cx + CAT_W - SCROLLBAR_W || my < cy || my >= cy + CAT_H) return false;

        List<CatalogueEntry> entries = handler.getEntries();
        int idx = scrollOffset + (int) (my - cy) / ROW_H;
        if (idx >= entries.size()) return false;

        CatalogueEntry entry = entries.get(idx);
        int lvX = cx + CAT_W - SCROLLBAR_W - 8 - entry.maxLevel() * 14;
        int level = 1;
        if (mx >= lvX) {
            int lvIdx = (int) (mx - lvX) / 14;
            if (lvIdx >= 0 && lvIdx < entry.maxLevel()) level = lvIdx + 1;
        }

        handler.setSelection(idx, level);
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        client.interactionManager.clickButton(handler.syncId, idx * 10 + (level - 1));
        return true;
    }

    private boolean clickScroll(double mx, double my, int x, int y) {
        int sx = x + CAT_X + CAT_W - SCROLLBAR_W - 1;
        int sy = y + CAT_Y;
        if (mx >= sx && mx < sx + SCROLLBAR_W && my >= sy && my < sy + CAT_H && shouldScroll()) {
            scrolling = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        if (scrolling && shouldScroll()) {
            float top = this.y + CAT_Y;
            scrollAmount = MathHelper.clamp((float) (click.y() - top) / CAT_H, 0, 1);
            scrollOffset = (int) (scrollAmount * getMaxScroll());
            return true;
        }
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseReleased(Click click) {
        scrolling = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmt, double vAmt) {
        if (super.mouseScrolled(mx, my, hAmt, vAmt)) return true;
        if (!shouldScroll()) return false;
        int max = getMaxScroll();
        scrollOffset = MathHelper.clamp(scrollOffset - (int) vAmt, 0, max);
        scrollAmount = max > 0 ? (float) scrollOffset / max : 0;
        return true;
    }

    private boolean shouldScroll() { return handler.getEntries().size() > VISIBLE_ROWS; }
    private int getMaxScroll() { return Math.max(0, handler.getEntries().size() - VISIBLE_ROWS); }

    // --- Drawing helpers ---

    private void border3D(DrawContext ctx, int x, int y, int w, int h, int light, int dark) {
        ctx.fill(x, y, x + w, y + 1, light);
        ctx.fill(x, y, x + 1, y + h, light);
        ctx.fill(x + w - 1, y + 1, x + w, y + h, dark);
        ctx.fill(x + 1, y + h - 1, x + w, y + h, dark);
    }

    private void borderInset(DrawContext ctx, int x, int y, int w, int h) {
        border3D(ctx, x, y, w, h, BORDER_D, BORDER_L);
    }

    private void slotBorder(DrawContext ctx, int x, int y) {
        ctx.fill(x, y, x + 18, y + 1, BORDER_D);
        ctx.fill(x, y, x + 1, y + 18, BORDER_D);
        ctx.fill(x + 17, y + 1, x + 18, y + 18, BORDER_L);
        ctx.fill(x + 1, y + 17, x + 18, y + 18, BORDER_L);
        ctx.fill(x + 1, y + 1, x + 17, y + 17, SLOT_BG);
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(n);
        };
    }
}
