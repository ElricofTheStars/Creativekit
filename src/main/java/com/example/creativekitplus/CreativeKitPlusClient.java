package com.example.creativekitplus;

import com.example.creativekitplus.config.Config;
import com.example.creativekitplus.keybind.Keybinds;
import com.example.creativekitplus.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

/**
 * Client entrypoint. Every stage is isolated and logged so that if anything
 * fails at runtime, latest.log shows exactly where. On the first tick with a
 * player it also prints a chat line, giving visible proof the tick loop runs.
 */
public class CreativeKitPlusClient implements ClientModInitializer {

    public static final String MOD_ID = "creativekitplus";

    private static ModuleManager moduleManager;
    private static boolean greeted = false;

    private static void log(String msg) {
        System.out.println("[CreativeKitPlus] " + msg);
    }

    @Override
    public void onInitializeClient() {
        log("onInitializeClient START");

        try {
            Config.load();
            log("config loaded");
        } catch (Throwable t) {
            log("config load FAILED: " + t);
            t.printStackTrace();
        }

        try {
            Keybinds.register();
            log("keybinds registered: " + Keybinds.count() + " keys");
        } catch (Throwable t) {
            log("keybind registration FAILED: " + t);
            t.printStackTrace();
        }

        try {
            moduleManager = new ModuleManager();
            log("modules built");
        } catch (Throwable t) {
            log("module build FAILED: " + t);
            t.printStackTrace();
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (!greeted && client.player != null) {
                    greeted = true;
                    log("first tick with player - mod is ALIVE, tick loop running");
                    client.player.sendMessage(
                            Text.literal("CreativeKit Plus loaded - press R for the menu"), false);
                }
                if (moduleManager != null) {
                    Keybinds.handleToggles(client, moduleManager);
                    moduleManager.tick(client);
                }
            } catch (Throwable t) {
                log("tick error: " + t);
                t.printStackTrace();
            }
        });

        log("onInitializeClient DONE - tick handler registered");
    }

    public static ModuleManager modules() {
        return moduleManager;
    }
}
