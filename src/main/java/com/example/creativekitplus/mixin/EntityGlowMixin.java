package com.example.creativekitplus.mixin;

import com.example.creativekitplus.module.EntityHighlighter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces {@code Entity#isGlowing()} to return true for entities the highlighter
 * targets. This is a purely visual client override, equivalent to the vanilla
 * glowing outline, so it works with team/outline colors automatically.
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void creativekitplus$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (EntityHighlighter.shouldGlow(self)) {
            cir.setReturnValue(true);
        }
    }
}
