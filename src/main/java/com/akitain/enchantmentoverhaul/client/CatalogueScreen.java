package com.akitain.enchantmentoverhaul.client;

import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler;
import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler.CatalogueEntry;
import com.akitain.enchantmentoverhaul.enchant.EnchantmentCosts;
import com.akitain.enchantmentoverhaul.enchant.SlotSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class CatalogueScreen extends AbstractContainerScreen<CatalogueScreenHandler> {

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

    private static final Identifier SLOT_SWORD = Identifier.withDefaultNamespace("container/slot/sword");
    private static final Identifier SLOT_AMETHYST = Identifier.withDefaultNamespace("container/slot/amethyst_shard");

    private static final Identifier[] SLOT_PLACEHOLDERS = { SLOT_SWORD, SLOT_AMETHYST };

    private static final Style SGA_STYLE = Style.EMPTY
            .withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "alt")));
    private static final String SGA_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private final String[] sgaRows = new String[20];

    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");

    private net.minecraft.client.model.object.book.BookModel bookModel;
    private float scrollAmount;
    private int scrollOffset;
    private boolean scrolling;
    private ItemStack lastItem = ItemStack.EMPTY;

    public CatalogueScreen(CatalogueScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, BG_W, BG_H);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 110;
        for (int i = 0; i < sgaRows.length; i++) sgaRows[i] = randomSga(18);
    }

    @Override
    protected void init() {
        super.init();
        menu.rebuildEntries();
        bookModel = new net.minecraft.client.model.object.book.BookModel(
                net.minecraft.client.model.object.book.BookModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float deltaTicks) {
        checkItemChanged();
        super.extractRenderState(gfx, mouseX, mouseY, deltaTicks);
        gfx.text(font, this.title, this.leftPos + titleLabelX, this.topPos + titleLabelY, 0xFF404040, false);
        drawCatalogueTooltip(gfx, mouseX, mouseY);
    }

    private void checkItemChanged() {
        ItemStack current = menu.getSlot(0).getItem();
        if (!ItemStack.matches(current, lastItem)) {
            lastItem = current.copy();
            menu.rebuildEntries();
            scrollAmount = 0;
            scrollOffset = 0;
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float deltaTicks) {
        int x = this.leftPos, y = this.topPos;

        drawRoundedFrame(gfx, x, y, BG_W, BG_H);
        drawBook(gfx, x, y);
        drawInputSlots(gfx, x, y);
        drawArrow(gfx, x, y);
        drawOutputSlot(gfx, x, y);
        drawCatalogue(gfx, x, y, mouseX, mouseY);
        drawSlotBar(gfx, x, y);
        drawPlayerSlotBorders(gfx, x, y);

        super.extractContents(gfx, mouseX, mouseY, deltaTicks);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        gfx.text(font, this.playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    private void drawRoundedFrame(GuiGraphicsExtractor ctx, int x, int y, int w, int h) {
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

    private void drawInputSlots(GuiGraphicsExtractor gfx, int x, int y) {
        for (int i = 0; i < 2; i++) {
            Slot slot = menu.slots.get(i);
            slotBorder(gfx, x + slot.x - 1, y + slot.y - 1);
            if (slot.getItem().isEmpty()) {
                gfx.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_PLACEHOLDERS[i],
                        x + slot.x, y + slot.y, 16, 16);
            }
        }
    }

    private void drawBook(GuiGraphicsExtractor gfx, int x, int y) {
        if (bookModel == null) return;
        int bx = x + BOOK_X;
        int by = y + BOOK_Y;
        gfx.book(bookModel, BOOK_TEXTURE, 40.0f, 0.9f, 0.1f, bx, by, bx + 50, by + 40);
    }

    private void drawArrow(GuiGraphicsExtractor gfx, int x, int y) {
        int cx = x + 28;
        int ay = y + 78;
        gfx.fill(cx - 1, ay, cx + 1, ay + 3, ARROW_COLOR);
        gfx.fill(cx - 3, ay + 3, cx + 3, ay + 4, ARROW_COLOR);
        gfx.fill(cx - 2, ay + 4, cx + 2, ay + 5, ARROW_COLOR);
        gfx.fill(cx - 1, ay + 5, cx + 1, ay + 6, ARROW_COLOR);
    }

    private void drawOutputSlot(GuiGraphicsExtractor gfx, int x, int y) {
        Slot slot = menu.slots.get(2);
        slotBorder(gfx, x + slot.x - 1, y + slot.y - 1);
    }

    private void drawCatalogue(GuiGraphicsExtractor gfx, int x, int y, int mouseX, int mouseY) {
        int cx = x + CAT_X, cy = y + CAT_Y;

        gfx.fill(cx, cy, cx + CAT_W, cy + CAT_H, PANEL_BG);
        borderInset(gfx, cx, cy, CAT_W, CAT_H);

        List<CatalogueEntry> entries = menu.getEntries();
        if (entries.isEmpty()) {
            String hint = menu.getSlot(0).getItem().isEmpty()
                    ? "Insert item"
                    : "Unlock enchantments with bookshelves";
            List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.literal(hint), CAT_W - 8);
            int totalH = lines.size() * (font.lineHeight + 2);
            int startY = cy + (CAT_H - totalH) / 2;
            for (int i = 0; i < lines.size(); i++) {
                int lw = font.width(lines.get(i));
                gfx.text(font, lines.get(i),
                        cx + (CAT_W - lw) / 2,
                        startY + i * (font.lineHeight + 2), 0xFF808080, false);
            }
            return;
        }

        int rowW = shouldScroll() ? CAT_W - SCROLLBAR_W - 4 : CAT_W - 4;
        int end = Math.min(scrollOffset + VISIBLE_ROWS, entries.size());
        for (int i = scrollOffset; i < end; i++) {
            int row = i - scrollOffset;
            int ry = cy + 2 + row * (ROW_H + 1);
            drawRow(gfx, entries.get(i), i, cx + 2, ry, rowW, mouseX, mouseY);
        }

        drawScrollbar(gfx, cx + CAT_W - SCROLLBAR_W - 2, cy + 2, CAT_H - 4);
    }

    private void drawRow(GuiGraphicsExtractor gfx, CatalogueEntry entry, int idx, int rx, int ry, int rw, int mx, int my) {
        boolean selected = idx == menu.getSelectedIndex();
        boolean hovered = mx >= rx && mx < rx + rw && my >= ry && my < ry + ROW_H;

        int bg = selected ? ROW_SELECTED : (hovered ? ROW_HOVER : ROW_BG);
        gfx.fill(rx, ry, rx + rw, ry + ROW_H, bg);

        int borderTop = selected ? 0xFF9A6090 : ROW_BORDER_L;
        int borderBot = selected ? 0xFF60305A : ROW_BORDER_D;
        gfx.fill(rx, ry, rx + rw, ry + 1, borderTop);
        gfx.fill(rx, ry + ROW_H - 1, rx + rw, ry + ROW_H, borderBot);

        int lvX = rx + rw - 2 - entry.maxLevel() * (LV_BTN + LV_GAP);
        int sgaMaxX = lvX - 3;

        int sgaColor = selected ? 0xFFE0C0E0 : (hovered ? 0xFFB0A080 : 0xFF988870);
        String sga = sgaRows[idx % sgaRows.length];
        int sgaY = ry + (ROW_H - 8) / 2;
        Component sgaText = Component.literal(trimToWidth(sga, sgaMaxX - rx - 3)).setStyle(SGA_STYLE);
        gfx.text(font, sgaText, rx + 3, sgaY, sgaColor, true);
        int selLv = selected ? menu.getSelectedLevel() : 0;

        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            boolean lvSel = selected && lv == selLv;
            int lvBg = lvSel ? 0xFF5A8830 : (selected ? 0xFF4A2848 : 0xFF3A3028);
            int lvBorderL = lvSel ? 0xFF7AB848 : (selected ? 0xFF6A4868 : 0xFF5A5048);
            int lvBorderD = lvSel ? 0xFF2A4810 : (selected ? 0xFF2A0828 : 0xFF1A1008);
            int lvY = ry + (ROW_H - LV_BTN) / 2;
            gfx.fill(lvX, lvY, lvX + LV_BTN, lvY + LV_BTN, lvBg);
            gfx.fill(lvX, lvY, lvX + LV_BTN, lvY + 1, lvBorderL);
            gfx.fill(lvX, lvY, lvX + 1, lvY + LV_BTN, lvBorderL);
            gfx.fill(lvX + LV_BTN - 1, lvY + 1, lvX + LV_BTN, lvY + LV_BTN, lvBorderD);
            gfx.fill(lvX + 1, lvY + LV_BTN - 1, lvX + LV_BTN, lvY + LV_BTN, lvBorderD);

            String r = toRoman(lv);
            int tw = font.width(r);
            int lvColor = lvSel ? 0xFFC0FF80 : (selected ? 0xFFB890B8 : 0xFF7A6A5A);
            gfx.text(font, r, lvX + (LV_BTN - tw) / 2, lvY + 3, lvColor, true);
            lvX += LV_BTN + LV_GAP;
        }
    }

    private void drawCatalogueTooltip(GuiGraphicsExtractor gfx, int mx, int my) {
        int cx = this.leftPos + CAT_X, cy = this.topPos + CAT_Y;
        if (mx < cx || mx >= cx + CAT_W || my < cy || my >= cy + CAT_H) return;

        List<CatalogueEntry> entries = menu.getEntries();
        int idx = scrollOffset + (my - cy - 2) / (ROW_H + 1);
        if (idx < 0 || idx >= entries.size()) return;

        CatalogueEntry entry = entries.get(idx);
        ResourceKey<Enchantment> key = entry.key();
        boolean selected = idx == menu.getSelectedIndex();
        int level = selected ? menu.getSelectedLevel() : 1;

        Item reagentItem = EnchantmentCosts.reagent(key);
        int reagentCost = EnchantmentCosts.reagentCost(level, menu.getNormalBookshelves());
        int xpCost = EnchantmentCosts.xpCost(key, level);
        int slotCost = EnchantmentCosts.slotCost(entry.entry(), level);

        ItemStack item = menu.getSlot(0).getItem();
        ItemStack reagent = menu.getSlot(1).getItem();
        int playerXp = Minecraft.getInstance().player.experienceLevel;

        boolean hasReagent = reagent.is(reagentItem) && reagent.getCount() >= reagentCost;
        boolean hasXp = playerXp >= xpCost;
        boolean hasSlots = SlotSystem.getAvailableSlots(item) >= slotCost;

        String levelLabel = selected ? " " + toRoman(level) : "";
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(entry.entry().value().description().getString() + levelLabel)
                .withStyle(entry.entry().is(EnchantmentTags.CURSE) ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.empty());
        tooltip.add(costLine(new ItemStack(reagentItem).getHoverName().getString(), reagentCost, hasReagent));
        tooltip.add(costLine("XP Levels", xpCost, hasXp));
        if (slotCost > 0) {
            tooltip.add(costLine("Slots", slotCost, hasSlots));
        } else {
            tooltip.add(Component.literal("Slots: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("+1").withStyle(ChatFormatting.GREEN)));
        }

        if (menu.getNormalBookshelves() > 0) {
            int pct = (int) (EnchantmentCosts.baseReagentCost(level) > 0
                    ? (1.0 - (double) reagentCost / EnchantmentCosts.baseReagentCost(level)) * 100 : 0);
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Bookshelves: " + menu.getNormalBookshelves() + " (-" + pct + "% reagent)")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        gfx.setComponentTooltipForNextFrame(font, tooltip, mx, my);
    }

    private Component costLine(String label, int amount, boolean has) {
        ChatFormatting color = has ? ChatFormatting.GREEN : ChatFormatting.RED;
        return Component.literal(label + ": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(amount)).withStyle(color));
    }

    private void drawScrollbar(GuiGraphicsExtractor gfx, int sx, int sy, int sh) {
        if (!shouldScroll()) return;
        gfx.fill(sx, sy, sx + SCROLLBAR_W, sy + sh, 0xFF2A2218);

        int thumbH = Math.max(10, sh * VISIBLE_ROWS / menu.getEntries().size());
        int thumbY = sy + (int) ((sh - thumbH) * scrollAmount);
        gfx.fill(sx, thumbY, sx + SCROLLBAR_W, thumbY + thumbH, SLOT_BG);
        border3D(gfx, sx, thumbY, SCROLLBAR_W, thumbH, 0xFFC6C6C6, 0xFF555555);
    }

    private void drawSlotBar(GuiGraphicsExtractor gfx, int x, int y) {
        ItemStack item = menu.getSlot(0).getItem();
        if (item.isEmpty()) return;

        int max = SlotSystem.getMaxSlots(item);
        int used = SlotSystem.getUsedSlots(item);
        if (max <= 0) return;

        int pendingCost = 0;
        if (menu.getSelectedIndex() >= 0 && menu.getSelectedIndex() < menu.getEntries().size()) {
            CatalogueEntry e = menu.getEntries().get(menu.getSelectedIndex());
            pendingCost = EnchantmentCosts.slotCost(e.entry(), menu.getSelectedLevel());
        }

        int barY = y + SLOT_BAR_Y;
        int ph = 6;
        String countText = used + "/" + max;
        int countW = font.width(countText);
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
            gfx.fill(px, barY, px + pw, barY + ph, pipBg);
            gfx.fill(px, barY, px + pw, barY + 1, pipBorderL);
            gfx.fill(px, barY, px + 1, barY + ph, pipBorderL);
            gfx.fill(px + pw - 1, barY + 1, px + pw, barY + ph, pipBorderD);
            gfx.fill(px + 1, barY + ph - 1, px + pw, barY + ph, pipBorderD);
        }

        int textX = barRight + 4;
        gfx.text(font, countText, textX, barY - 1, TEXT_LIGHT, true);
    }

    private void drawPlayerSlotBorders(GuiGraphicsExtractor gfx, int x, int y) {
        for (int i = 3; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            slotBorder(gfx, x + slot.x - 1, y + slot.y - 1);
        }
    }

    // --- Input ---

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int x = this.leftPos, y = this.topPos;

        if (clickRow(mx, my, x, y)) return true;
        if (clickScroll(mx, my, x, y)) return true;

        return super.mouseClicked(click, doubled);
    }

    private boolean clickRow(double mx, double my, int x, int y) {
        int cx = x + CAT_X, cy = y + CAT_Y;
        int rowW = shouldScroll() ? CAT_W - SCROLLBAR_W - 4 : CAT_W - 4;
        if (mx < cx + 2 || mx >= cx + 2 + rowW || my < cy + 2 || my >= cy + CAT_H - 2) return false;

        List<CatalogueEntry> entries = menu.getEntries();
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

        menu.setSelection(idx, level);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, idx * 10 + (level - 1));
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
    public boolean mouseDragged(MouseButtonEvent click, double dx, double dy) {
        if (scrolling && shouldScroll()) {
            float top = this.topPos + CAT_Y + 2;
            float sh = CAT_H - 4;
            scrollAmount = Mth.clamp((float) (click.y() - top) / sh, 0, 1);
            scrollOffset = (int) (scrollAmount * getMaxScroll());
            return true;
        }
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        scrolling = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmt, double vAmt) {
        if (super.mouseScrolled(mx, my, hAmt, vAmt)) return true;
        if (!shouldScroll()) return false;
        int max = getMaxScroll();
        scrollOffset = Mth.clamp(scrollOffset - (int) vAmt, 0, max);
        scrollAmount = max > 0 ? (float) scrollOffset / max : 0;
        return true;
    }

    private boolean shouldScroll() { return menu.getEntries().size() > VISIBLE_ROWS; }
    private int getMaxScroll() { return Math.max(0, menu.getEntries().size() - VISIBLE_ROWS); }

    // --- Drawing helpers ---

    private void border3D(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int light, int dark) {
        ctx.fill(x, y, x + w, y + 1, light);
        ctx.fill(x, y, x + 1, y + h, light);
        ctx.fill(x + w - 1, y + 1, x + w, y + h, dark);
        ctx.fill(x + 1, y + h - 1, x + w, y + h, dark);
    }

    private void borderInset(GuiGraphicsExtractor ctx, int x, int y, int w, int h) {
        border3D(ctx, x, y, w, h, BORDER_D, BORDER_L);
    }

    private void slotBorder(GuiGraphicsExtractor ctx, int x, int y) {
        ctx.fill(x, y, x + 18, y + 1, BORDER_D);
        ctx.fill(x, y, x + 1, y + 18, BORDER_D);
        ctx.fill(x + 17, y + 1, x + 18, y + 18, BORDER_L);
        ctx.fill(x + 1, y + 17, x + 18, y + 18, BORDER_L);
        ctx.fill(x + 1, y + 1, x + 17, y + 17, SLOT_BG);
    }

    private String trimToWidth(String text, int maxWidth) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
            w += font.width(Component.literal(String.valueOf(text.charAt(i))).setStyle(SGA_STYLE));
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
