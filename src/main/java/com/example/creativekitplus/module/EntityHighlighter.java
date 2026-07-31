package com.example.creativekitplus.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;

/**
 * Forces a glowing outline on living entities. The actual glow is applied by
 * {@code EntityGlowMixin}, which reads {@link #shouldGlow}. This module just
 * carries the toggle so the mixin can stay stateless.
 */
public final class EntityHighlighter extends AbstractModule {

    /** Read by the mixin. Static so the mixin needs no instance reference. */
    private static volatile boolean active = false;

    public EntityHighlighter(boolean defaultEnabled) {
        super("Entity Highlight", defaultEnabled);
        active = defaultEnabled;
    }

    @Override
    public void onTick(MinecraftClient client) {
        active = isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        active = enabled;
    }

    /** Called from EntityGlowMixin. Return true to force a glow on this entity. */
    public static boolean shouldGlow(Entity entity) {
        if (!active) return false;
        if (entity.isSpectator()) return false;
        // Highlight all living mobs. Narrow this if you only want hostiles:
        //   return entity instanceof HostileEntity;
        return entity instanceof LivingEntity;
    }
}
