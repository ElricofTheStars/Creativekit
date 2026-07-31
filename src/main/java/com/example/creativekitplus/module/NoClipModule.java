package com.example.creativekitplus.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Phase through blocks. Sets the entity noClip flag each tick so collision is
 * skipped client-side. Pair with Flight, otherwise you just fall through the
 * world. Strongly single-player: the integrated/remote server still tracks a
 * collided position and will resync you if used against authoritative movement.
 */
public final class NoClipModule extends AbstractModule {

    public NoClipModule(boolean defaultEnabled) {
        super("No-Clip", defaultEnabled);
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        // Entity#noClip is a public field in yarn mappings.
        player.noClip = true;
    }

    @Override
    public void onDisable(MinecraftClient client) {
        if (client.player != null) {
            client.player.noClip = false;
        }
    }
}
