package com.example.creativekitplus.module;

import com.example.creativekitplus.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * X-ray: hides all blocks except a configured allow-list (ores by default).
 * The face-culling decision lives in {@code BlockShouldDrawSideMixin}, which
 * calls {@link #isVisible}. Toggling forces a world re-render so chunks rebuild.
 *
 * Note: this is single-player oriented. X-ray is banned on essentially every
 * multiplayer server and many run server-side detection for it.
 */
public final class XrayModule extends AbstractModule {

    private static volatile boolean active = false;

    public XrayModule(boolean defaultEnabled) {
        super("X-Ray", defaultEnabled);
        active = defaultEnabled;
    }

    @Override
    public void onTick(MinecraftClient client) {
        // No per-tick work; state changes trigger the re-render below.
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean changed = enabled != active;
        super.setEnabled(enabled);
        active = enabled;
        if (changed) reloadChunks();
    }

    public static boolean isActive() {
        return active;
    }

    /** True if the block should still render while X-ray is on. */
    public static boolean isVisible(BlockState state) {
        Block block = state.getBlock();
        Identifier id = Registries.BLOCK.getId(block);
        return Config.get().xrayVisibleBlocks.contains(id.toString());
    }

    private void reloadChunks() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.worldRenderer != null) {
            mc.execute(mc.worldRenderer::reload);
        }
    }
}
