package com.example.creativekitplus.module;

import net.minecraft.client.MinecraftClient;

/**
 * A single self-contained feature. Each module owns its own enabled state
 * and reacts on the client tick. Keep all logic client-side.
 */
public interface Module {

    /** Called every client tick while the game world is loaded. */
    void onTick(MinecraftClient client);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    /** Human-readable name used for chat/HUD feedback. */
    String getName();

    /** Called once when the module is toggled off, for cleanup. */
    default void onDisable(MinecraftClient client) {
    }
}
