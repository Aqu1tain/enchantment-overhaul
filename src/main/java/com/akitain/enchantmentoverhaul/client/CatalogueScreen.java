package com.akitain.enchantmentoverhaul.client;

import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler;
import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler.CatalogueEntry;
import com.akitain.enchantmentoverhaul.enchant.EnchantmentCosts;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class CatalogueScreen extends HandledScreen<CatalogueScreenHandler> {

    private static final int BG_W = 176;
    private static final int BG_H = 200;

    private static final int CAT_X = 56;
    private static final int CAT_Y = 16;
    private static final int CAT_W = 114;
    private static final int ROW_H = 19;
    private static final int VISIBLE_ROWS = 4;
    private static final int CAT_H = 86;
    private static final int SCROLLBAR_W = 7;

    private static final int BOOK_X = 3;
    private static final int BOOK_Y = 16;

    private static final int LV_BTN = 14;
    private static final int LV_GAP = 1;

    private static final int SLOT_BAR_Y = 104;

    private static final int ROW_BG = 0xFF56493D;
    private static final int ROW_HOVER = 0xFF6B5D4E;
    private static final int ROW_SELECTED = 0xFF80507A;
    private static final int ROW_BORDER_L = 0xFF7A6B5A;
    private static final int ROW_BORDER_D = 0xFF3A3028;

    private static final int PANEL_BG = 0xFF585858;
    private static final int TEXT_LIGHT = 0xFFD8C8F0;
    private static final int BG = 0xFFC6C6C6;
    private static final int SLOT_BG = 0xFF8B8B8B;
    private static final int BORDER_L = 0xFFFFFFFF;
    private static final int BORDER_D = 0xFF373737;
    private static final int ARROW_COLOR = 0xFF9E9E9E;

    private static final Identifier SLOT_SWORD = new Identifier("item/empty_slot_sword");
    private static final Identifier SLOT_AMETHYST = new Identifier("item/empty_slot_amethyst_shard");

    private static final Identifier[] SLOT_PLACEHOLDERS = { SLOT_SWORD, SLOT_AMETHYST };

    private static final Style SGA_STYLE = Style.EMPTY.withFont(new Identifier("minecraft", "alt"));
    private static final String SGA_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private final String[] sgaRows = new String[20];

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
        this.playerInventoryTitleX = 7;
        this.playerInventoryTitleY = 110;
        for (int i = 0; i < sgaRows.length; i++) sgaRows[i] = randomSga(18);
    }

    @Override
    protected void init() {
        super.init();
        handler.rebuildEntries();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        checkItemChanged();
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, this.title, this.x + titleX, this.y + titleY, 0xFF404040, false);
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x, y = this.y;

        drawRoundedFrame(context, x, y, BG_W, BG_H);
        drawInputSlots(context, x, y);
        drawArrow(context, x, y);
        drawOutputSlot(context, x, y);
        drawCatalogue(context, x, y, mouseX, mouseY);
        drawSlotBar(context, x, y);
        drawPlayerSlotBorders(context, x, y);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, this.playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0x404040, false);
    }

    private void drawRoundedFrame(DrawContext ctx, int x, int y, int w, int h) {
        int L = BORDER_L, D = BORDER_D;
        ctx.fill(x + 2, y + 2, x + w - 2, y + h - 2, BG);
        ctx.fill(x + 1, y, x + w - 1, y + 1, L);
        ctx.fill(x, y + 1, x + w, y + 2, L);
        ctx.fill(x, y + h - 2, x + w, y + h - 1, D);
        ctx.fill(x + 1, y + h - 1, x + w - 1, y + h, D);
        ctx.fill(x, y + 1, x + 1, y + h - 1, L);
        ctx.fill(x + 1, y, x + 2, y + h, L);
        ctx.fill(x + w - 2, y, x + w - 1, y + h, D);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, D);
    }

    private void drawInputSlots(DrawContext context, int x, int y) {
        for (int i = 0; i < 2; i++) {
            Slot slot = handler.slots.get(i);
            slotBorder(context, x + slot.x - 1, y + slot.y - 1);
        }
    }

    private void drawArrow(DrawContext context, int x, int y) {
        int cx = x + 28;
        int ay = y + 78;
        context.fill(cx - 1, ay, cx + 1, ay + 3, ARROW_COLOR);
        context.fill(cx - 3, ay + 3, cx + 3, ay + 4, ARROW_COLOR);
        context.fill(cx - 2, ay + 4, cx + 2, ay + 5, ARROW_COLOR);
        context.fill(cx - 1, ay + 5, cx + 1, ay + 6, ARROW_COLOR);
    }

    private void drawOutputSlot(DrawContext context, int x, int y) {
        Slot slot = handler.slots.get(2);
        slotBorder(context, x + slot.x - 1, y + slot.y - 1);
    }

    private void drawCatalogue(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int cx = x + CAT_X, cy = y + CAT_Y;

        context.fill(cx, cy, cx + CAT_W, cy + CAT_H, PANEL_BG);
        borderInset(context, cx, cy, CAT_W, CAT_H);

        List<CatalogueEntry> entries = handler.getEntries();
        if (entries.isEmpty()) {
            String hint = handler.getSlot(0).getStack().isEmpty()
                    ? "Insert item"
                    : "Unlock enchantments with bookshelves";
            List<net.minecraft.text.OrderedText> lines = textRenderer.wrapLines(Text.literal(hint), CAT_W - 8);
            int totalH = lines.size() * (textRenderer.fontHeight + 2);
            int startY = cy + (CAT_H - totalH) / 2;
            for (int i = 0; i < lines.size(); i++) {
                int lw = textRenderer.getWidth(lines.get(i));
                context.drawText(textRenderer, lines.get(i),
                        cx + (CAT_W - lw) / 2,
                        startY + i * (textRenderer.fontHeight + 2), 0xFF808080, false);
            }
            return;
        }

        int rowW = shouldScroll() ? CAT_W - SCROLLBAR_W - 4 : CAT_W - 4;
        int end = Math.min(scrollOffset + VISIBLE_ROWS, entries.size());
        for (int i = scrollOffset; i < end; i++) {
            int row = i - scrollOffset;
            int ry = cy + 2 + row * (ROW_H + 1);
            drawRow(context, entries.get(i), i, cx + 2, ry, rowW, mouseX, mouseY);
        }

        drawScrollbar(context, cx + CAT_W - SCROLLBAR_W - 2, cy + 2, CAT_H - 4);
    }

    private void drawRow(DrawContext context, CatalogueEntry entry, int idx, int rx, int ry, int rw, int mx, int my) {
        boolean selected = idx == handler.getSelectedIndex();
        boolean hovered = mx >= rx && mx < rx + rw && my >= ry && my < ry + ROW_H;

        int bg = selected ? ROW_SELECTED : (hovered ? ROW_HOVER : ROW_BG);
        context.fill(rx, ry, rx + rw, ry + ROW_H, bg);

        int borderTop = selected ? 0xFF9A6090 : ROW_BORDER_L;
        int borderBot = selected ? 0xFF60305A : ROW_BORDER_D;
        context.fill(rx, ry, rx + rw, ry + 1, borderTop);
        context.fill(rx, ry + ROW_H - 1, rx + rw, ry + ROW_H, borderBot);

        int lvX = rx + rw - 2 - entry.maxLevel() * (LV_BTN + LV_GAP);
        int sgaMaxX = lvX - 3;

        int sgaColor = selected ? 0xFFE0C0E0 : (hovered ? 0xFFB0A080 : 0xFF988870);
        String sga = sgaRows[idx % sgaRows.length];
        int sgaY = ry + (ROW_H - 8) / 2;
        Text sgaText = Text.literal(trimToWidth(sga, sgaMaxX - rx - 3)).setStyle(SGA_STYLE);
        context.drawTextWithShadow(textRenderer, sgaText, rx + 3, sgaY, sgaColor);
        int selLv = selected ? handler.getSelectedLevel() : 0;

        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            boolean lvSel = selected && lv == selLv;
            int lvBg = lvSel ? 0xFF5A8830 : (selected ? 0xFF4A2848 : 0xFF3A3028);
            int lvBorderL = lvSel ? 0xFF7AB848 : (selected ? 0xFF6A4868 : 0xFF5A5048);
            int lvBorderD = lvSel ? 0xFF2A4810 : (selected ? 0xFF2A0828 : 0xFF1A1008);
            int lvY = ry + (ROW_H - LV_BTN) / 2;
            context.fill(lvX, lvY, lvX + LV_BTN, lvY + LV_BTN, lvBg);
            context.fill(lvX, lvY, lvX + LV_BTN, lvY + 1, lvBorderL);
            context.fill(lvX, lvY, lvX + 1, lvY + LV_BTN, lvBorderL);
            context.fill(lvX + LV_BTN - 1, lvY + 1, lvX + LV_BTN, lvY + LV_BTN, lvBorderD);
            context.fill(lvX + 1, lvY + LV_BTN - 1, lvX + LV_BTN, lvY + LV_BTN, lvBorderD);

            String r = toRoman(lv);
            int tw = textRenderer.getWidth(r);
            int lvColor = lvSel ? 0xFFC0FF80 : (selected ? 0xFFB890B8 : 0xFF7A6A5A);
            context.drawTextWithShadow(textRenderer, r, lvX + (LV_BTN - tw) / 2, lvY + 3, lvColor);
            lvX += LV_BTN + LV_GAP;
        }
    }

    private void drawCatalogueTooltip(DrawContext context, int mx, int my) {
        int cx = this.x + CAT_X, cy = this.y + CAT_Y;
        if (mx < cx || mx >= cx + CAT_W || my < cy || my >= cy + CAT_H) return;

        List<CatalogueEntry> entries = handler.getEntries();
        int idx = scrollOffset + (my - cy - 2) / (ROW_H + 1);
        if (idx < 0 || idx >= entries.size()) return;

        CatalogueEntry entry = entries.get(idx);
        Enchantment enchantment = entry.enchantment();
        boolean selected = idx == handler.getSelectedIndex();
        int level = selected ? handler.getSelectedLevel() : 1;

        Item reagentItem = EnchantmentCosts.reagent(enchantment);
        int reagentCost = EnchantmentCosts.reagentCost(level, handler.getNormalBookshelves());
        int xpCost = EnchantmentCosts.xpCost(enchantment, level);
        int slotCost = EnchantmentCosts.slotCost(enchantment, level);

        ItemStack item = handler.getSlot(0).getStack();
        ItemStack reagent = handler.getSlot(1).getStack();
        int playerXp = MinecraftClient.getInstance().player.experienceLevel;

        boolean hasReagent = reagent.isOf(reagentItem) && reagent.getCount() >= reagentCost;
        boolean hasXp = playerXp >= xpCost;
        boolean hasSlots = SlotSystem.getAvailableSlots(item) >= slotCost;

        String levelLabel = selected ? " " + toRoman(level) : "";
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.literal(Text.translatable(enchantment.getTranslationKey()).getString() + levelLabel)
                .formatted(enchantment.isCursed() ? Formatting.RED : Formatting.LIGHT_PURPLE));
        tooltip.add(Text.empty());
        tooltip.add(costLine(reagentItem.getName().getString(), reagentCost, hasReagent));
        tooltip.add(costLine("XP Levels", xpCost, hasXp));
        if (slotCost > 0) {
            tooltip.add(costLine("Slots", slotCost, hasSlots));
        } else {
            tooltip.add(Text.literal("Slots: ").formatted(Formatting.GRAY)
                    .append(Text.literal("+1").formatted(Formatting.GREEN)));
        }

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
        if (!shouldScroll()) return;
        context.fill(sx, sy, sx + SCROLLBAR_W, sy + sh, 0xFF2A2218);

        int thumbH = Math.max(10, sh * VISIBLE_ROWS / handler.getEntries().size());
        int thumbY = sy + (int) ((sh - thumbH) * scrollAmount);
        context.fill(sx, thumbY, sx + SCROLLBAR_W, thumbY + thumbH, SLOT_BG);
        border3D(context, sx, thumbY, SCROLLBAR_W, thumbH, 0xFFC6C6C6, 0xFF555555);
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
            pendingCost = EnchantmentCosts.slotCost(e.enchantment(), handler.getSelectedLevel());
        }

        int barY = y + SLOT_BAR_Y;
        int ph = 6;
        String countText = used + "/" + max;
        int countW = textRenderer.getWidth(countText);
        int barLeft = x + CAT_X;
        int barRight = x + CAT_X + CAT_W - countW - 4;
        int availableW = barRight - barLeft;
        int gap = max > 1 ? Math.max(1, Math.min(2, (availableW - max * 4) / (max - 1))) : 2;
        int pw = Math.max(4, (availableW - gap * (max - 1)) / max);
        int barX = barLeft;

        for (int i = 0; i < max; i++) {
            int px = barX + i * (pw + gap);
            int pipBg, pipBorderL, pipBorderD;
            if (i < used) {
                pipBg = 0xFF7B48B8; pipBorderL = 0xFF9B68D8; pipBorderD = 0xFF3B1878;
            } else if (i < used + pendingCost) {
                boolean blink = (System.currentTimeMillis() / 400) % 2 == 0;
                pipBg = blink ? 0xFFB880F0 : 0xFF5030A0;
                pipBorderL = 0xFFD8A0FF; pipBorderD = 0xFF6840A0;
            } else {
                pipBg = 0xFF2A1848; pipBorderL = 0xFF3A2858; pipBorderD = 0xFF1A0838;
            }
            context.fill(px, barY, px + pw, barY + ph, pipBg);
            context.fill(px, barY, px + pw, barY + 1, pipBorderL);
            context.fill(px, barY, px + 1, barY + ph, pipBorderL);
            context.fill(px + pw - 1, barY + 1, px + pw, barY + ph, pipBorderD);
            context.fill(px + 1, barY + ph - 1, px + pw, barY + ph, pipBorderD);
        }

        int textX = barRight + 4;
        context.drawTextWithShadow(textRenderer, countText, textX, barY - 1, TEXT_LIGHT);
    }

    private void drawPlayerSlotBorders(DrawContext context, int x, int y) {
        for (int i = 3; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            slotBorder(context, x + slot.x - 1, y + slot.y - 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.x, y = this.y;

        if (clickRow(mouseX, mouseY, x, y)) return true;
        if (clickScroll(mouseX, mouseY, x, y)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickRow(double mx, double my, int x, int y) {
        int cx = x + CAT_X, cy = y + CAT_Y;
        int rowW = shouldScroll() ? CAT_W - SCROLLBAR_W - 4 : CAT_W - 4;
        if (mx < cx + 2 || mx >= cx + 2 + rowW || my < cy + 2 || my >= cy + CAT_H - 2) return false;

        List<CatalogueEntry> entries = handler.getEntries();
        int idx = scrollOffset + (int) (my - cy - 2) / (ROW_H + 1);
        if (idx < 0 || idx >= entries.size()) return false;

        CatalogueEntry entry = entries.get(idx);
        int rx = cx + 2;
        int lvX = rx + rowW - 2 - entry.maxLevel() * (LV_BTN + LV_GAP);
        int level = 1;
        if (mx >= lvX) {
            int lvIdx = (int) (mx - lvX) / (LV_BTN + LV_GAP);
            if (lvIdx >= 0 && lvIdx < entry.maxLevel()) level = lvIdx + 1;
        }

        handler.setSelection(idx, level);
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        client.interactionManager.clickButton(handler.syncId, idx * 10 + (level - 1));
        return true;
    }

    private boolean clickScroll(double mx, double my, int x, int y) {
        int sx = x + CAT_X + CAT_W - SCROLLBAR_W - 2;
        int sy = y + CAT_Y + 2;
        int sh = CAT_H - 4;
        if (mx >= sx && mx < sx + SCROLLBAR_W && my >= sy && my < sy + sh && shouldScroll()) {
            scrolling = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrolling && shouldScroll()) {
            float top = this.y + CAT_Y + 2;
            float sh = CAT_H - 4;
            scrollAmount = MathHelper.clamp((float) (mouseY - top) / sh, 0, 1);
            scrollOffset = (int) (scrollAmount * getMaxScroll());
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!shouldScroll()) return false;
        int max = getMaxScroll();
        scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, max);
        scrollAmount = max > 0 ? (float) scrollOffset / max : 0;
        return true;
    }

    private boolean shouldScroll() { return handler.getEntries().size() > VISIBLE_ROWS; }
    private int getMaxScroll() { return Math.max(0, handler.getEntries().size() - VISIBLE_ROWS); }

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

    private String trimToWidth(String text, int maxWidth) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            w += textRenderer.getWidth(Text.literal(String.valueOf(text.charAt(i))).setStyle(SGA_STYLE));
            if (w > maxWidth) return text.substring(0, i);
        }
        return text;
    }

    private static String randomSga(int length) {
        Random rng = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(SGA_CHARS.charAt(rng.nextInt(SGA_CHARS.length())));
        return sb.toString();
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
