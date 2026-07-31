package com.example.creativekitplus.module;

import com.example.creativekitplus.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Multiplies horizontal movement while the player is actively giving input.
 * Leaves vertical velocity untouched so jumping/falling stay normal. Skips
 * when Flight is driving velocity to avoid the two fighting.
 */
public final class SpeedModifier extends AbstractModule {

    private final FlightController flight;

    public SpeedModifier(boolean defaultEnabled, FlightController flight) {
        super("Speed", defaultEnabled);
        this.flight = flight;
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        if (flight != null && flight.isEnabled()) return;

        boolean moving = client.options.forwardKey.isPressed()
                || client.options.backKey.isPressed()
                || client.options.leftKey.isPressed()
                || client.options.rightKey.isPressed();
        if (!moving) return;

        double mult = Math.max(1.0, Config.get().speedMultiplier);
        Vec3d v = player.getVelocity();
        player.setVelocity(v.x * mult, v.y, v.z * mult);
    }
}
