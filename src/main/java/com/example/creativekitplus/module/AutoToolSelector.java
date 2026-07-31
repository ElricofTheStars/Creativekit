package com.example.creativekitplus.module;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

/**
 * When the attack key is held and the crosshair is on a block, switches the
 * hotbar to whichever slot mines that block fastest. Only touches the hotbar
 * (slots 0-8), which the client is allowed to select freely.
 */
public final class AutoToolSelector extends AbstractModule {

    public AutoToolSelector(boolean defaultEnabled) {
        super("Auto Tool", defaultEnabled);
    }

    @Override
    public void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit)) return;
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = client.world.getBlockState(pos);
        if (state.isAir()) return;

        int bestSlot = player.getInventory().selectedSlot;
        float bestSpeed = miningSpeed(player.getInventory().getStack(bestSlot), state);

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) continue;
            float speed = miningSpeed(stack, state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }

        if (bestSlot != player.getInventory().selectedSlot) {
            player.getInventory().selectedSlot = bestSlot;
        }
    }

    /**
     * Mining speed of a stack against a block state.
     * NOTE: verify the exact method name for 1.21.11 yarn. Recent builds use
     * ItemStack#getMiningSpeedMultiplier(BlockState). If renamed, adjust here only.
     */
    private static float miningSpeed(ItemStack stack, BlockState state) {
        if (stack.isEmpty()) return 1.0f;
        return stack.getMiningSpeedMultiplier(state);
    }
}
