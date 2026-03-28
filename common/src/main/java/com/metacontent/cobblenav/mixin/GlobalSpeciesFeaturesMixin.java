package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.pokemon.feature.GlobalSpeciesFeatures;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider;
import com.metacontent.cobblenav.util.DirectFeatureRegistrar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(GlobalSpeciesFeatures.class)
public abstract class GlobalSpeciesFeaturesMixin implements DirectFeatureRegistrar {
    @Shadow
    @Final
    private static Map<String, SpeciesFeatureProvider<?>> codeFeatures;

    @Override
    public void cobblenav$registerDirectly(String name, SpeciesFeatureProvider<?> provider) {
        codeFeatures.put(name, provider);
    }
}
