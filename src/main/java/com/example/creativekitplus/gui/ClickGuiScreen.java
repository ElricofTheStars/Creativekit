package com.example.creativekitplus.gui;

import com.example.creativekitplus.config.Config;
import com.example.creativekitplus.module.Module;
import com.example.creativekitplus.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Simple click GUI. Opened with Right Shift.
 *   - Left-click a module row  -> toggle it on/off.
 *   - Left-click the [-] / [+]  -> change that module's numeric setting.
 * Right Shift or Escape closes. The world keeps ticking (shouldPause = false).
 *
 * Rendering uses only DrawContext.fill + drawText and manual hit-testing, which
 * is the most version-tolerant approach across 1.21.x. If your build's
 * renderBackground signature differs, that single call is the only thing to fix.
 */
public class ClickGuiScreen extends Screen {

    // ---- layout ----
    private static final int PANEL_W = 232;
    private static final int ROW_H = 24;
    private static final int HEADER_H = 28;
    private static final int PAD = 6;
    private static final int BTN = 16;

    // ---- colors (ARGB) ----
    private static final int C_PANEL   = 0xE0181A1F;
    private static final int C_HEADER  = 0xFF2A2E37;
    private static final int C_ROW     = 0xFF23262E;
    private static final int C_ROW_HOV = 0xFF2E323C;
    private static final int C_ON      = 0xFF55E06B;
    private static final int C_OFF     = 0xFFE05555;
    private static final int C_BTN     = 0xFF3A3F4B;
    private static final int C_BTN_HOV = 0xFF4A5060;
    private static final int C_TEXT    = 0xFFFFFFFF;
    private static final int C_DIM     = 0xFFB8BEC9;

    private final ModuleManager mm;
    private final List<Entry> entries = new ArrayList<>();

    public ClickGuiScreen(ModuleManager mm) {
        super(Text.literal("CreativeKit Plus"));
        this.mm = mm;
    }

    @Override
    protected void init() {
        entries.clear();
        Config c = Config.get();

        // Modules with a numeric setting expose min/max/step + a live value.
        entries.add(Entry.withSetting(mm.flight(), "Flight Speed",
                () -> c.flightSpeed, v -> c.flightSpeed = v, 0.5, 3.0, 0.25));
        entries.add(Entry.toggleOnly(mm.xray()));
        entries.add(Entry.toggleOnly(mm.highlighter()));
        entries.add(Entry.toggleOnly(mm.autoTool()));
        entries.add(Entry.toggleOnly(mm.noClip()));
        entries.add(Entry.withSetting(mm.speed(), "Multiplier",
                () -> c.speedMultiplier, v -> c.speedMultiplier = v, 1.0, 5.0, 0.25));
        entries.add(Entry.toggleOnly(mm.fullBright()));
    }

    private int panelX() { return (this.width - PANEL_W) / 2; }
    private int panelH() { return HEADER_H + entries.size() * ROW_H + PAD; }
    private int panelY() { return (this.height - panelH()) / 2; }
    private int rowTop(int i) { return panelY() + HEADER_H + i * ROW_H; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        int px = panelX(), py = panelY();
        ctx.fill(px, py, px + PANEL_W, py + panelH(), C_PANEL);
        ctx.fill(px, py, px + PANEL_W, py + HEADER_H, C_HEADER);
        ctx.drawText(this.textRenderer, Text.literal("CreativeKit Plus"),
                px + PAD, py + PAD + 2, C_TEXT, false);
        ctx.drawText(this.textRenderer, Text.literal("Right Shift / Esc to close"),
                px + PANEL_W - 128, py + PAD + 3, C_DIM, false);

        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            int top = rowTop(i);
            boolean hovRow = inRect(mouseX, mouseY, px, top, PANEL_W, ROW_H);
            ctx.fill(px, top, px + PANEL_W, top + ROW_H, hovRow ? C_ROW_HOV : C_ROW);

            boolean on = e.module.isEnabled();
            ctx.fill(px + PAD, top + 6, px + PAD + 4, top + ROW_H - 6, on ? C_ON : C_OFF);
            ctx.drawText(this.textRenderer, Text.literal(e.module.getName()),
                    px + PAD + 12, top + 8, on ? C_TEXT : C_DIM, false);

            if (e.hasSetting()) {
                int plusX  = px + PANEL_W - PAD - BTN;
                int minusX = plusX - BTN - 46;
                int by = top + (ROW_H - BTN) / 2;

                drawBtn(ctx, minusX, by, "-", inRect(mouseX, mouseY, minusX, by, BTN, BTN));
                drawBtn(ctx, plusX,  by, "+", inRect(mouseX, mouseY, plusX,  by, BTN, BTN));

                String val = String.format("%.2f", e.get.get());
                int valX = minusX + BTN + (46 - this.textRenderer.getWidth(val)) / 2;
                ctx.drawText(this.textRenderer, Text.literal(val), valX, top + 8, C_TEXT, false);
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawBtn(DrawContext ctx, int x, int y, String label, boolean hov) {
        ctx.fill(x, y, x + BTN, y + BTN, hov ? C_BTN_HOV : C_BTN);
        int tx = x + (BTN - this.textRenderer.getWidth(label)) / 2;
        ctx.drawText(this.textRenderer, Text.literal(label), tx, y + 4, C_TEXT, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int px = panelX();
            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                int top = rowTop(i);

                if (e.hasSetting()) {
                    int plusX  = px + PANEL_W - PAD - BTN;
                    int minusX = plusX - BTN - 46;
                    int by = top + (ROW_H - BTN) / 2;
                    if (inRect(mouseX, mouseY, plusX, by, BTN, BTN))  { e.step(+1); return true; }
                    if (inRect(mouseX, mouseY, minusX, by, BTN, BTN)) { e.step(-1); return true; }
                }

                // Anywhere else on the row toggles the module.
                if (inRect(mouseX, mouseY, px, top, PANEL_W, ROW_H)) {
                    mm.toggle(e.module);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        Config.save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    // ---------------------------------------------------------------

    private interface DblConsumer { void accept(double v); }

    private static final class Entry {
        final Module module;
        final String settingLabel;
        final Supplier<Double> get;
        final DblConsumer set;
        final double min, max, step;

        private Entry(Module module, String settingLabel, Supplier<Double> get,
                      DblConsumer set, double min, double max, double step) {
            this.module = module;
            this.settingLabel = settingLabel;
            this.get = get;
            this.set = set;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        static Entry toggleOnly(Module m) {
            return new Entry(m, null, null, null, 0, 0, 0);
        }

        static Entry withSetting(Module m, String label, Supplier<Double> get,
                                 DblConsumer set, double min, double max, double step) {
            return new Entry(m, label, get, set, min, max, step);
        }

        boolean hasSetting() { return get != null; }

        void step(int dir) {
            double next = MathHelper.clamp(get.get() + dir * step, min, max);
            next = Math.round(next / step) * step;
            set.accept(next);
        }
    }
}
