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
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class CatalogueScreen extends HandledScreen<CatalogueScreenHandler> {

    private static final int BG_W = 280;
    private static final int BG_H = 230;

    private static final int CAT_X = 52;
    private static final int CAT_Y = 18;
    private static final int CAT_W = 220;
    private static final int ROW_H = 20;
    private static final int VISIBLE_ROWS = 5;
    private static final int CAT_H = ROW_H * VISIBLE_ROWS;
    private static final int SCROLLBAR_W = 6;

    private static final int BTN_X = 6;
    private static final int BTN_Y = 108;
    private static final int BTN_W = 38;
    private static final int BTN_H = 14;

    private static final int SLOT_BAR_Y = 124;

    // Vanilla-inspired row colors
    private static final int ROW_BG = 0xFF56493D;
    private static final int ROW_HOVER = 0xFF6B5D4E;
    private static final int ROW_SELECTED = 0xFF80507A;
    private static final int ROW_BORDER_L = 0xFF7A6B5A;
    private static final int ROW_BORDER_D = 0xFF3A3028;

    // General colors
    private static final int PANEL_BG = 0xFF1A0A2E;
    private static final int PURPLE_MID = 0xFF2D1250;
    private static final int PURPLE_LIGHT = 0xFF8050C0;
    private static final int PURPLE_BRIGHT = 0xFFC080FF;
    private static final int TEXT_LIGHT = 0xFFE0D8F0;
    private static final int TEXT_DIM = 0xFF9080B0;
    private static final int GOLD = 0xFFFFAA00;
    private static final int BG = 0xFFC6C6C6;
    private static final int SLOT_BG = 0xFF8B8B8B;
    private static final int BORDER_L = 0xFFFFFFFF;
    private static final int BORDER_D = 0xFF555555;

    private static final Style SGA_STYLE = Style.EMPTY
            .withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "alt")));
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
        this.playerInventoryTitleX = 59;
        this.playerInventoryTitleY = BG_H - 94;
        for (int i = 0; i < sgaRows.length; i++) sgaRows[i] = randomSga(28);
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

    // --- Drawing ---

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = this.x, y = this.y;

        context.fill(x, y, x + BG_W, y + BG_H, BG);
        border3D(context, x, y, BG_W, BG_H, BORDER_L, BORDER_D);

        drawBook(context, x, y);
        drawInputSlots(context, x, y);
        drawCatalogue(context, x, y, mouseX, mouseY);
        drawSlotBar(context, x, y);
        drawEnchantButton(context, x, y, mouseX, mouseY);
        drawPlayerSlotBorders(context, x, y);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, Text.literal("Enchant"), titleX, titleY, 0x404040, false);
        context.drawText(textRenderer, this.playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0x404040, false);
    }

    private void drawBook(DrawContext context, int x, int y) {
        int bx = x + 10, by = y + 18;
        // Spine
        context.fill(bx + 15, by, bx + 17, by + 22, 0xFF5C3A1E);
        // Left page
        context.fill(bx + 2, by + 1, bx + 15, by + 21, 0xFFD8C8A0);
        context.fill(bx + 1, by + 2, bx + 2, by + 20, 0xFFD8C8A0);
        // Right page
        context.fill(bx + 17, by + 1, bx + 30, by + 21, 0xFFD8C8A0);
        context.fill(bx + 30, by + 2, bx + 31, by + 20, 0xFFD8C8A0);
        // Left cover edges
        context.fill(bx, by + 2, bx + 1, by + 20, 0xFF8B5A2B);
        context.fill(bx + 1, by, bx + 15, by + 1, 0xFF8B5A2B);
        context.fill(bx + 1, by + 21, bx + 15, by + 22, 0xFF6B3A1B);
        // Right cover edges
        context.fill(bx + 31, by + 2, bx + 32, by + 20, 0xFF8B5A2B);
        context.fill(bx + 17, by, bx + 31, by + 1, 0xFF8B5A2B);
        context.fill(bx + 17, by + 21, bx + 31, by + 22, 0xFF6B3A1B);
        // Page lines (left)
        for (int i = 0; i < 4; i++) {
            int ly = by + 5 + i * 4;
            context.fill(bx + 4, ly, bx + 13, ly + 1, 0x30000000);
        }
        // Page lines (right)
        for (int i = 0; i < 4; i++) {
            int ly = by + 5 + i * 4;
            context.fill(bx + 19, ly, bx + 28, ly + 1, 0x30000000);
        }
    }

    private void drawInputSlots(DrawContext context, int x, int y) {
        for (int i = 0; i < 3; i++) {
            Slot slot = handler.slots.get(i);
            slotBorder(context, x + slot.x - 1, y + slot.y - 1);
        }
    }

    private void drawCatalogue(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int cx = x + CAT_X, cy = y + CAT_Y;

        context.fill(cx, cy, cx + CAT_W, cy + CAT_H, PANEL_BG);
        borderInset(context, cx, cy, CAT_W, CAT_H);

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

        int rowW = CAT_W - SCROLLBAR_W - 4;
        int end = Math.min(scrollOffset + VISIBLE_ROWS, entries.size());
        for (int i = scrollOffset; i < end; i++) {
            int row = i - scrollOffset;
            int ry = cy + 1 + row * ROW_H;
            drawRow(context, entries.get(i), i, cx + 1, ry, rowW, mouseX, mouseY);
        }

        drawScrollbar(context, cx + CAT_W - SCROLLBAR_W - 1, cy + 1, CAT_H - 2);
    }

    private void drawRow(DrawContext context, CatalogueEntry entry, int idx, int rx, int ry, int rw, int mx, int my) {
        boolean selected = idx == handler.getSelectedIndex();
        boolean hovered = mx >= rx && mx < rx + rw && my >= ry && my < ry + ROW_H;

        int bg = selected ? ROW_SELECTED : (hovered ? ROW_HOVER : ROW_BG);
        context.fill(rx, ry, rx + rw, ry + ROW_H - 1, bg);

        // Top/bottom row border
        int borderTop = selected ? 0xFF9A6090 : ROW_BORDER_L;
        int borderBot = selected ? 0xFF60305A : ROW_BORDER_D;
        context.fill(rx, ry, rx + rw, ry + 1, borderTop);
        context.fill(rx, ry + ROW_H - 2, rx + rw, ry + ROW_H - 1, borderBot);

        // SGA decoration text (subtle behind the name)
        int sgaColor = selected ? 0xFF9A6898 : 0xFF6B5D50;
        String sga = sgaRows[idx % sgaRows.length];
        context.drawText(textRenderer, Text.literal(sga).setStyle(SGA_STYLE), rx + 4, ry + 6, sgaColor, false);

        // Enchantment name overlaid
        int ty = ry + (ROW_H - 9) / 2;
        boolean curse = entry.entry().isIn(EnchantmentTags.CURSE);
        int nameColor = selected ? 0xFFFFFFFF : (curse ? 0xFFFF6666 : 0xFFE8DCC8);
        String name = entry.entry().value().description().getString();
        context.drawTextWithShadow(textRenderer, name, rx + 4, ty, nameColor);

        // Level buttons right-aligned
        int lvX = rx + rw - 2 - entry.maxLevel() * 14;
        int selLv = selected ? handler.getSelectedLevel() : 0;

        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            boolean lvSel = selected && lv == selLv;
            int lvBg = lvSel ? 0xFF60A040 : (selected ? 0xFF603060 : 0xFF4A3E34);
            context.fill(lvX, ry + 3, lvX + 12, ry + ROW_H - 4, lvBg);
            String r = toRoman(lv);
            int tw = textRenderer.getWidth(r);
            int lvColor = lvSel ? 0xFF80FF40 : (selected ? 0xFFC0A0C0 : 0xFF8A7A6A);
            context.drawTextWithShadow(textRenderer, r, lvX + (12 - tw) / 2, ty, lvColor);
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

        if (SlotSystem.getAvailableSlots(item) < EnchantmentCosts.slotCost(key, level)) return false;
        if (MinecraftClient.getInstance().player.isCreative()) return true;

        return lapis.getCount() >= EnchantmentCosts.lapisCost(level)
                && reagent.isOf(EnchantmentCosts.reagent(key))
                && reagent.getCount() >= EnchantmentCosts.reagentCost(level, handler.getNormalBookshelves())
                && MinecraftClient.getInstance().player.experienceLevel >= EnchantmentCosts.xpCost(key, level);
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
        int rowW = CAT_W - SCROLLBAR_W - 4;
        if (mx < cx + 1 || mx >= cx + 1 + rowW || my < cy || my >= cy + CAT_H) return false;

        List<CatalogueEntry> entries = handler.getEntries();
        int idx = scrollOffset + (int) (my - cy) / ROW_H;
        if (idx >= entries.size()) return false;

        CatalogueEntry entry = entries.get(idx);
        int rx = cx + 1;
        int lvX = rx + rowW - 2 - entry.maxLevel() * 14;
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
