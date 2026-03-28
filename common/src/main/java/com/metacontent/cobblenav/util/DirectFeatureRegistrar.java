package com.metacontent.cobblenav.util;

import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider;

public interface DirectFeatureRegistrar {
    void cobblenav$registerDirectly(String name, SpeciesFeatureProvider<?> provider);
}
