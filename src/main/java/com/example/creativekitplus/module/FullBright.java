package com.example.creativekitplus.module;

import net.minecraft.client.MinecraftClient;

/**
 * Full-brightness. Pushes the gamma (brightness) option to a high value each
 * tick and restores the player's original value on disable.
 *
 * Version note: some builds clamp the gamma option's stored value to <= 1.0
 * inside SimpleOption. If you find max brightness capped, the standard fix is a
 * small mixin that overrides the gamma option's value getter to return a large
 * constant while {@link #isActive()} is true. Left out here to avoid shipping an
 * unverified mixin target; the direct-set path below works on builds that keep
 * the stored double unclamped.
 */
public final class FullBright extends AbstractModule {

    private static volatile boolean active = false;
    private static final double BRIGHT = 15.0;

    private Double savedGamma = null;

    public FullBright(boolean defaultEnabled) {
        super("Full Bright", defaultEnabled);
        active = defaultEnabled;
    }

    @Override
    public void onTick(MinecraftClient client) {
        active = isEnabled();
        if (client.options == null) return;
        if (savedGamma == null) {
            savedGamma = client.options.getGamma().getValue();
        }
        client.options.getGamma().setValue(BRIGHT);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        active = enabled;
    }

    @Override
    public void onDisable(MinecraftClient client) {
        if (savedGamma != null && client.options != null) {
            client.options.getGamma().setValue(savedGamma);
            savedGamma = null;
        }
    }

    public static boolean isActive() {
        return active;
    }
}
