package com.example.creativekitplus.module;

import com.example.creativekitplus.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Velocity-based flight for building. Works by driving the player's velocity
 * each tick from the movement keys, independent of the vanilla fly ability
 * flags (which the server owns). Best used single-player where you host the
 * integrated server; on remote servers movement is authoritative and this
 * will rubber-band or be rejected.
 */
public final class FlightController extends AbstractModule {

    public FlightController(boolean defaultEnabled) {
        super("Flight", defaultEnabled);
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        GameOptions o = client.options;
        double base = 0.5 * Config.get().flightSpeed;

        double forward = 0, strafe = 0, vertical = 0;
        if (o.forwardKey.isPressed()) forward += 1;
        if (o.backKey.isPressed())    forward -= 1;
        if (o.leftKey.isPressed())    strafe  += 1;
        if (o.rightKey.isPressed())   strafe  -= 1;
        if (o.jumpKey.isPressed())    vertical += 1;
        if (o.sneakKey.isPressed())   vertical -= 1;

        // Rotate forward/strafe into world space using the player's yaw.
        float yawRad = (float) Math.toRadians(player.getYaw());
        double sin = MathHelper.sin(yawRad);
        double cos = MathHelper.cos(yawRad);

        double vx = (forward * -sin + strafe * -cos) * base;
        double vz = (forward *  cos + strafe * -sin) * base;
        double vy = vertical * base;

        player.setVelocity(new Vec3d(vx, vy, vz));
        player.fallDistance = 0.0f;
        player.setOnGround(false);
    }

    @Override
    public void onDisable(MinecraftClient client) {
        if (client.player != null) {
            client.player.setVelocity(Vec3d.ZERO);
        }
    }
}
