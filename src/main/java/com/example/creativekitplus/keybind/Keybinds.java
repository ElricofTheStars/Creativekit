package com.example.creativekitplus.keybind;

import com.example.creativekitplus.gui.ClickGuiScreen;
import com.example.creativekitplus.module.Module;
import com.example.creativekitplus.module.ModuleManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Registers toggle keys and routes presses to the matching module. All binds
 * default to unbound-friendly keys under the "CreativeKit Plus" category and
 * can be rebound in Options > Controls.
 */
public final class Keybinds {

    // 1.21.11 replaced the String category with a KeyBinding.Category record.
    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("creativekitplus", "main"));

    private static KeyBinding flightKey;
    private static KeyBinding xrayKey;
    private static KeyBinding highlightKey;
    private static KeyBinding autoToolKey;
    private static KeyBinding noClipKey;
    private static KeyBinding speedKey;
    private static KeyBinding fullBrightKey;
    private static KeyBinding openGuiKey;

    private Keybinds() {}

    public static void register() {
        flightKey     = bind("flight",     GLFW.GLFW_KEY_V);  // F collides with swap-hand
        xrayKey       = bind("xray",       GLFW.GLFW_KEY_X);
        highlightKey  = bind("highlight",  GLFW.GLFW_KEY_H);
        autoToolKey   = bind("autotool",   GLFW.GLFW_KEY_Y);  // T collides with chat
        noClipKey     = bind("noclip",     GLFW.GLFW_KEY_N);
        speedKey      = bind("speed",      GLFW.GLFW_KEY_G);
        fullBrightKey = bind("fullbright", GLFW.GLFW_KEY_B);
        openGuiKey    = bind("gui",        GLFW.GLFW_KEY_R);  // Right Shift is an unreliable modifier
    }

    private static KeyBinding bind(String id, int glfwKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.creativekitplus." + id,
                InputUtil.Type.KEYSYM,
                glfwKey,
                CATEGORY));
    }

    /** Call once per client tick. Consumes queued presses so a hold = one toggle. */
    public static void handleToggles(MinecraftClient client, ModuleManager mm) {
        if (flightKey.wasPressed())     announce(client, mm, mm.flight());
        if (xrayKey.wasPressed())       announce(client, mm, mm.xray());
        if (highlightKey.wasPressed())  announce(client, mm, mm.highlighter());
        if (autoToolKey.wasPressed())   announce(client, mm, mm.autoTool());
        if (noClipKey.wasPressed())     announce(client, mm, mm.noClip());
        if (speedKey.wasPressed())      announce(client, mm, mm.speed());
        if (fullBrightKey.wasPressed()) announce(client, mm, mm.fullBright());

        if (openGuiKey.wasPressed() && client.currentScreen == null) {
            client.setScreen(new ClickGuiScreen(mm));
        }
    }

    private static void announce(MinecraftClient client, ModuleManager mm, Module m) {
        boolean on = mm.toggle(m);
        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal("[CKP] " + m.getName() + ": " + (on ? "ON" : "OFF")),
                    true /* actionbar */);
        }
    }
}
