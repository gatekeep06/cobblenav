package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.metacontent.cobblenav.Cobblenav;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cobblemon.class)
public abstract class CobblemonMixin {
    @Inject(method = "initialize", at = @At("HEAD"), remap = false)
    protected void injectInitialize(CallbackInfo ci) {
        Cobblenav.INSTANCE.onCobblemonInit();
    }
}
