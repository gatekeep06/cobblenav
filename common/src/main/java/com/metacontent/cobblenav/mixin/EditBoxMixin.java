package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.client.gui.screen.pokefinder.AdvancedPokefinderScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V")
    )
    private void drawPokefinderCaret(GuiGraphics instance, RenderType renderType, int i, int j, int k, int l, int m) {
        Screen screen = Minecraft.getInstance().screen;
        boolean isPokefinder = screen instanceof AdvancedPokefinderScreen;
        instance.fill(renderType, i, j, k, l, isPokefinder ? ((AdvancedPokefinderScreen) screen).getColor() : m);
    }
}
