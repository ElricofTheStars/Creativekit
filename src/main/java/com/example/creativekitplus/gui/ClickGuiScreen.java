package com.example.creativekitplus.gui;

import com.example.creativekitplus.config.Config;
import com.example.creativekitplus.module.Module;
import com.example.creativekitplus.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * Click GUI, opened with Right Shift (see Keybinds).
 *
 * Built entirely from ButtonWidgets. Widgets handle their own clicks through
 * the vanilla element system, so this class never overrides mouseClicked /
 * keyPressed. That matters on 1.21.11, where those signatures changed to the
 * new Click / KeyInput objects. Escape closes the screen (vanilla default) or
 * use the Done button. The world keeps ticking while it's open.
 */
public class ClickGuiScreen extends Screen {

    private static final int W = 220;   // row width
    private static final int H = 20;    // row height
    private static final int GAP = 4;

    private final ModuleManager mm;

    public ClickGuiScreen(ModuleManager mm) {
        super(Text.literal("CreativeKit Plus"));
        this.mm = mm;
    }

    @Override
    protected void init() {
        Config c = Config.get();
        int x = this.width / 2 - W / 2;

        // Total rows: 7 toggles + 2 setting rows + 1 done = 10.
        int rows = 10;
        int y = this.height / 2 - (rows * (H + GAP)) / 2;

        y = addToggle(x, y, mm.flight());
        y = addSetting(x, y, "Flight Speed",
                () -> c.flightSpeed, v -> c.flightSpeed = v, 0.5, 3.0, 0.25);
        y = addToggle(x, y, mm.xray());
        y = addToggle(x, y, mm.highlighter());
        y = addToggle(x, y, mm.autoTool());
        y = addToggle(x, y, mm.noClip());
        y = addToggle(x, y, mm.speed());
        y = addSetting(x, y, "Speed x",
                () -> c.speedMultiplier, v -> c.speedMultiplier = v, 1.0, 5.0, 0.25);
        y = addToggle(x, y, mm.fullBright());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> this.close())
                .dimensions(x, y, W, H).build());
    }

    private int addToggle(int x, int y, Module m) {
        addDrawableChild(ButtonWidget.builder(toggleLabel(m), btn -> {
            mm.toggle(m);                 // flips + saves config
            btn.setMessage(toggleLabel(m));
        }).dimensions(x, y, W, H).build());
        return y + H + GAP;
    }

    private int addSetting(int x, int y, String label,
                           DoubleSupplier get, DoubleConsumer set,
                           double min, double max, double step) {
        int side = 24;
        int mid = W - side * 2 - GAP * 2;

        // Inactive value button in the middle just displays the number.
        ButtonWidget value = ButtonWidget.builder(valueLabel(label, get.getAsDouble()), b -> {})
                .dimensions(x + side + GAP, y, mid, H).build();
        value.active = false;

        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> {
            double next = round(MathHelper.clamp(get.getAsDouble() - step, min, max), step);
            set.accept(next);
            value.setMessage(valueLabel(label, next));
            Config.save();
        }).dimensions(x, y, side, H).build());

        addDrawableChild(value);

        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> {
            double next = round(MathHelper.clamp(get.getAsDouble() + step, min, max), step);
            set.accept(next);
            value.setMessage(valueLabel(label, next));
            Config.save();
        }).dimensions(x + side + GAP + mid + GAP, y, side, H).build());

        return y + H + GAP;
    }

    private static Text toggleLabel(Module m) {
        return Text.literal(m.getName() + ": " + (m.isEnabled() ? "ON" : "OFF"));
    }

    private static Text valueLabel(String label, double v) {
        return Text.literal(label + "  " + String.format("%.2f", v));
    }

    private static double round(double v, double step) {
        return Math.round(v / step) * step;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title,
                this.width / 2, this.height / 2 - (10 * (H + GAP)) / 2 - 14, 0xFFFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
