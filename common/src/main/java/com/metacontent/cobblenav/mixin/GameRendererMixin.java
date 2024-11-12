package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.util.CustomizableBlurEffectProcessor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
abstract public class GameRendererMixin implements CustomizableBlurEffectProcessor {
    @Shadow @Nullable private PostChain blurEffect;

    @Override
    public void cobblenav$processBlurEffect(float blur, float delta) {
        if (this.blurEffect != null && blur >= 1f) {
            this.blurEffect.setUniform("Radius", blur);
            this.blurEffect.process(delta);
        }
    }
}
