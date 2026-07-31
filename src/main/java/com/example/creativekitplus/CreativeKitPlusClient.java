package com.example.creativekitplus;

import com.example.creativekitplus.config.Config;
import com.example.creativekitplus.keybind.Keybinds;
import com.example.creativekitplus.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Client entrypoint. Loads config, registers keybinds, builds the module
 * manager, and drives everything off the client tick event bus.
 */
public class CreativeKitPlusClient implements ClientModInitializer {

    public static final String MOD_ID = "creativekitplus";

    private static ModuleManager moduleManager;

    @Override
    public void onInitializeClient() {
        Config.load();
        Keybinds.register();
        moduleManager = new ModuleManager();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Keybinds.handleToggles(client, moduleManager);
            moduleManager.tick(client);
        });
    }

    public static ModuleManager modules() {
        return moduleManager;
    }
}
