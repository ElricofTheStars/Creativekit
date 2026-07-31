package com.example.creativekitplus.mixin;

import com.example.creativekitplus.module.XrayModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * X-ray face culling.
 *
 * When X-ray is active:
 *   - "visible" blocks (ore allow-list) always draw their faces, so they stand
 *     out even when embedded in stone.
 *   - every other block returns false, culling all faces, so it renders empty.
 *
 * IMPORTANT (1.21.11 mapping check): Block.shouldDrawSide has had two shapes
 * across recent versions. If the signature below does not match, switch to the
 * other and adjust the @Inject method descriptor accordingly:
 *
 *   (a) shouldDrawSide(BlockState state, BlockState otherState, Direction side)
 *   (b) shouldDrawSide(BlockState state, BlockView world, BlockPos pos,
 *                      Direction side, BlockPos otherPos)
 *
 * This mixin targets form (a). Verify on your build before relying on it.
 */
@Mixin(Block.class)
public abstract class BlockShouldDrawSideMixin {

    @Inject(method = "shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0) // require = 0 so a signature mismatch fails soft, not hard
    private static void creativekitplus$xray(BlockState state, BlockState otherState, Direction side,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!XrayModule.isActive()) return;

        if (XrayModule.isVisible(state)) {
            cir.setReturnValue(true);   // always show ores
        } else {
            cir.setReturnValue(false);  // hide everything else
        }
    }
}
