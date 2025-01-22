package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.client.CobblenavClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderTitle", at = @At("HEAD"))
    protected void beforeTitleRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        CobblenavClient.INSTANCE.renderOverlay(guiGraphics, deltaTracker);
    }
}
