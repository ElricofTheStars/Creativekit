package com.example.creativekitplus.module;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractModule implements Module {

    private final String name;
    private boolean enabled;

    protected AbstractModule(String name, boolean defaultEnabled) {
        this.name = name;
        this.enabled = defaultEnabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (!enabled) {
            onDisable(MinecraftClient.getInstance());
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
