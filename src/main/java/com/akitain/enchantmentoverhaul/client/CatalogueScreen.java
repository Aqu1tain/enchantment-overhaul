package com.akitain.enchantmentoverhaul.client;

import com.akitain.enchantmentoverhaul.EnchantmentOverhaul;
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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.entity.model.BookModel;
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

    private static final int TEXT_LIGHT = 0xFFD8C8F0;

    private static final Identifier SLOT_SWORD = Identifier.ofVanilla("container/slot/sword");
    private static final Identifier SLOT_AMETHYST = Identifier.ofVanilla("container/slot/amethyst_shard");
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/entity/enchanting_table_book.png");
    private static final Identifier BACKGROUND_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue.png");
    private static final Identifier ROW_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/row.png");
    private static final Identifier ROW_HOVER_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/row_hover.png");
    private static final Identifier ROW_SELECTED_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/row_selected.png");
    private static final Identifier ROW_DISABLED_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/row_disabled.png");
    private static final Identifier ROW_DISABLED_HOVER_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/row_disabled_hover.png");
    private static final Identifier LEVEL_SELECTED_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/level_selected.png");
    private static final Identifier LEVEL_SELECTED_AVAILABLE_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/level_selected_available.png");
    private static final Identifier LEVEL_SELECTED_UNAVAILABLE_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/level_selected_unavailable.png");
    private static final Identifier LEVEL_AVAILABLE_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/level_available.png");
    private static final Identifier LEVEL_UNAVAILABLE_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/level_unavailable.png");
    private static final Identifier SCROLLBAR_TRACK_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/scrollbar_track.png");
    private static final Identifier SCROLLBAR_THUMB_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/scrollbar_thumb.png");
    private static final Identifier SLOT_USED_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/slot_used.png");
    private static final Identifier SLOT_PENDING_ON_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/slot_pending_on.png");
    private static final Identifier SLOT_PENDING_OFF_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/slot_pending_off.png");
    private static final Identifier SLOT_FREE_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/slot_free.png");
    private static final Identifier SLOT_PENALTY_TEXTURE =
            Identifier.of(EnchantmentOverhaul.MOD_ID, "textures/gui/container/catalogue/slot_penalty.png");

    private static final Identifier[] SLOT_PLACEHOLDERS = { SLOT_SWORD, SLOT_AMETHYST };

    private static final Style SGA_STYLE = Style.EMPTY
            .withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "alt")));
    private static final String SGA_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private final String[] sgaRows = new String[20];

    private BookModel bookModel;
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
        bookModel = new BookModel(BookModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        checkItemChanged();
        super.render(context, mouseX, mouseY, deltaTicks);
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

    // --- Drawing ---

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = this.x, y = this.y;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, 0, 0, BG_W, BG_H, BG_W, BG_H);
        drawBook(context, x, y);
        drawSlotPlaceholders(context, x, y);
        drawCatalogue(context, x, y, mouseX, mouseY);
        drawSlotBar(context, x, y);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, this.playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0x404040, false);
    }

    private void drawBook(DrawContext context, int x, int y) {
        if (bookModel == null) return;
        int bx = x + BOOK_X;
        int by = y + BOOK_Y;
        context.addBookModel(bookModel, BOOK_TEXTURE, 40.0f, 0.9f, 0.1f,
                bx, by, bx + 50, by + 40);
    }

    private void drawSlotPlaceholders(DrawContext context, int x, int y) {
        for (int i = 0; i < 2; i++) {
            Slot slot = handler.slots.get(i);
            if (slot.getStack().isEmpty()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_PLACEHOLDERS[i],
                        x + slot.x, y + slot.y, 16, 16);
            }
        }
    }
    private void drawCatalogue(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int cx = x + CAT_X, cy = y + CAT_Y;

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

    private boolean isLevelAffordable(CatalogueEntry entry, int level) {
        if (level <= entry.currentLevel()) return false;

        ItemStack item = handler.getSlot(0).getStack();
        int slotCost = EnchantmentCosts.slotCost(entry.entry(), level, entry.currentLevel());
        if (SlotSystem.getAvailableSlots(item) < slotCost) return false;

        if (MinecraftClient.getInstance().player.isCreative()) return true;

        ItemStack reagent = handler.getSlot(1).getStack();
        Item reagentItem = EnchantmentCosts.reagent(entry.key());
        int reagentCost = EnchantmentCosts.reagentCost(level, entry.currentLevel(), handler.getNormalBookshelves());
        int xpCost = EnchantmentCosts.xpCost(entry.key(), level, entry.currentLevel());
        int playerXp = MinecraftClient.getInstance().player.experienceLevel;

        return reagent.isOf(reagentItem) && reagent.getCount() >= reagentCost && playerXp >= xpCost;
    }

    private boolean isAnyLevelAffordable(CatalogueEntry entry) {
        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            if (isLevelAffordable(entry, lv)) return true;
        }
        return false;
    }

    private void drawRow(DrawContext context, CatalogueEntry entry, int idx, int rx, int ry, int rw, int mx, int my) {
        boolean selected = idx == handler.getSelectedIndex();
        boolean hovered = mx >= rx && mx < rx + rw && my >= ry && my < ry + ROW_H;
        boolean affordable = isAnyLevelAffordable(entry);

        Identifier rowTexture;
        if (selected) {
            rowTexture = ROW_SELECTED_TEXTURE;
        } else if (affordable) {
            rowTexture = hovered ? ROW_HOVER_TEXTURE : ROW_TEXTURE;
        } else {
            rowTexture = hovered ? ROW_DISABLED_HOVER_TEXTURE : ROW_DISABLED_TEXTURE;
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, rowTexture, rx, ry, 0, 0, rw, ROW_H, 110, ROW_H);

        int lvX = rx + rw - 2 - entry.maxLevel() * (LV_BTN + LV_GAP);
        int sgaMaxX = lvX - 3;

        int sgaColor = selected ? 0xFFE0C0E0 : (affordable ? (hovered ? 0xFFB0A080 : 0xFF988870) : 0xFF605848);
        String sga = sgaRows[idx % sgaRows.length];
        int sgaY = ry + (ROW_H - 8) / 2;
        Text sgaText = Text.literal(trimToWidth(sga, sgaMaxX - rx - 3)).setStyle(SGA_STYLE);
        context.drawTextWithShadow(textRenderer, sgaText, rx + 3, sgaY, sgaColor);
        int selLv = selected ? handler.getSelectedLevel() : 0;

        for (int lv = 1; lv <= entry.maxLevel(); lv++) {
            boolean owned = lv <= entry.currentLevel();
            boolean lvSel = selected && lv == selLv;
            boolean lvAffordable = isLevelAffordable(entry, lv);

            Identifier lvTexture;
            int lvColor;
            if (owned) {
                lvTexture = LEVEL_UNAVAILABLE_TEXTURE;
                lvColor = 0xFF3E7A4E;
            } else if (lvSel) {
                lvTexture = LEVEL_SELECTED_TEXTURE;
                lvColor = 0xFFC0FF80;
            } else if (selected) {
                lvTexture = lvAffordable ? LEVEL_SELECTED_AVAILABLE_TEXTURE : LEVEL_SELECTED_UNAVAILABLE_TEXTURE;
                lvColor = lvAffordable ? 0xFFB890B8 : 0xFF685068;
            } else {
                lvTexture = lvAffordable ? LEVEL_AVAILABLE_TEXTURE : LEVEL_UNAVAILABLE_TEXTURE;
                lvColor = lvAffordable ? 0xFF7A6A5A : 0xFF504840;
            }

            int lvY = ry + (ROW_H - LV_BTN) / 2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, lvTexture, lvX, lvY, 0, 0, LV_BTN, LV_BTN, LV_BTN, LV_BTN);

            String r = toRoman(lv);
            int tw = textRenderer.getWidth(r);
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
        RegistryKey<Enchantment> key = entry.key();
        boolean selected = idx == handler.getSelectedIndex();

        int rowW = shouldScroll() ? CAT_W - SCROLLBAR_W - 4 : CAT_W - 4;
        int rx = cx + 2;
        int lvX = rx + rowW - 2 - entry.maxLevel() * (LV_BTN + LV_GAP);
        int level;
        if (mx >= lvX) {
            int lvIdx = (mx - lvX) / (LV_BTN + LV_GAP);
            level = Math.max(entry.currentLevel() + 1, Math.min(lvIdx + 1, entry.maxLevel()));
        } else if (selected) {
            level = handler.getSelectedLevel();
        } else {
            level = entry.currentLevel() + 1;
        }

        Item reagentItem = EnchantmentCosts.reagent(key);
        int reagentCost = EnchantmentCosts.reagentCost(level, entry.currentLevel(), handler.getNormalBookshelves());
        int xpCost = EnchantmentCosts.xpCost(key, level, entry.currentLevel());
        int slotCost = EnchantmentCosts.slotCost(entry.entry(), level, entry.currentLevel());

        ItemStack item = handler.getSlot(0).getStack();
        ItemStack reagent = handler.getSlot(1).getStack();
        int playerXp = MinecraftClient.getInstance().player.experienceLevel;

        boolean hasReagent = reagent.isOf(reagentItem) && reagent.getCount() >= reagentCost;
        boolean hasXp = playerXp >= xpCost;
        boolean hasSlots = SlotSystem.getAvailableSlots(item) >= slotCost;

        String levelLabel = entry.currentLevel() > 0
                ? " " + toRoman(entry.currentLevel()) + " → " + toRoman(level)
                : " " + toRoman(level);
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.literal(entry.entry().value().description().getString() + levelLabel)
                .formatted(entry.entry().isIn(EnchantmentTags.CURSE) ? Formatting.RED : Formatting.LIGHT_PURPLE));
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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, SCROLLBAR_TRACK_TEXTURE, sx, sy, 0, 0, SCROLLBAR_W, sh, SCROLLBAR_W, 82);

        int thumbH = Math.max(10, sh * VISIBLE_ROWS / handler.getEntries().size());
        int thumbY = sy + (int) ((sh - thumbH) * scrollAmount);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, SCROLLBAR_THUMB_TEXTURE, sx, thumbY, 0, 0, SCROLLBAR_W, thumbH, SCROLLBAR_W, 82);
    }

    private void drawSlotBar(DrawContext context, int x, int y) {
        ItemStack item = handler.getSlot(0).getStack();
        if (item.isEmpty()) return;

        int max = SlotSystem.getMaxSlots(item);
        int used = SlotSystem.getUsedSlots(item);
        int penalty = SlotSystem.getGrindstonePenalty(item);
        int totalPips = max + penalty;
        if (totalPips <= 0) return;

        int pendingCost = 0;
        if (handler.getSelectedIndex() >= 0 && handler.getSelectedIndex() < handler.getEntries().size()) {
            CatalogueEntry e = handler.getEntries().get(handler.getSelectedIndex());
            pendingCost = EnchantmentCosts.slotCost(e.entry(), handler.getSelectedLevel(), e.currentLevel());
        }

        int barY = y + SLOT_BAR_Y;
        int ph = 6;
        String countText = used + "/" + max;
        int countW = textRenderer.getWidth(countText);
        int barLeft = x + CAT_X;
        int barRight = x + CAT_X + CAT_W - countW - 4;
        int availableW = barRight - barLeft;
        int gap = totalPips > 1 ? Math.max(1, Math.min(2, (availableW - totalPips * 4) / (totalPips - 1))) : 2;
        int pw = Math.max(4, (availableW - gap * (totalPips - 1)) / totalPips);
        int barX = barLeft;

        for (int i = 0; i < totalPips; i++) {
            int px = barX + i * (pw + gap);
            Identifier pipTexture;
            if (i < used) {
                pipTexture = SLOT_USED_TEXTURE;
            } else if (i < used + pendingCost) {
                boolean blink = (System.currentTimeMillis() / 400) % 2 == 0;
                pipTexture = blink ? SLOT_PENDING_ON_TEXTURE : SLOT_PENDING_OFF_TEXTURE;
            } else if (i < max) {
                pipTexture = SLOT_FREE_TEXTURE;
            } else {
                pipTexture = SLOT_PENALTY_TEXTURE;
            }
            context.drawTexture(RenderPipelines.GUI_TEXTURED, pipTexture, px, barY, 0, 0, pw, ph, 128, ph);
        }

        int textX = barRight + 4;
        context.drawTextWithShadow(textRenderer, countText, textX, barY - 1, TEXT_LIGHT);
    }

    // --- Input ---
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int x = this.x, y = this.y;

        if (clickRow(mx, my, x, y)) return true;
        if (clickScroll(mx, my, x, y)) return true;

        return super.mouseClicked(click, doubled);
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
        int level = entry.currentLevel() + 1;
        if (mx >= lvX) {
            int lvIdx = (int) (mx - lvX) / (LV_BTN + LV_GAP);
            if (lvIdx >= 0 && lvIdx < entry.maxLevel()) level = lvIdx + 1;
        }

        if (!isLevelAffordable(entry, level)) return false;

        handler.setSelection(idx, level);
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
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
    public boolean mouseDragged(Click click, double dx, double dy) {
        if (scrolling && shouldScroll()) {
            float top = this.y + CAT_Y + 2;
            float sh = CAT_H - 4;
            scrollAmount = MathHelper.clamp((float) (click.y() - top) / sh, 0, 1);
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
