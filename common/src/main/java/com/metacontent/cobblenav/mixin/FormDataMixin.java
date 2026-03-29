package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.FormData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FormData.class)
public interface FormDataMixin {
    @Accessor("_evYield")
    void cobblenav$setEvYield(@Nullable Map<Stat, Integer> _evYield);

    @Accessor("_evYield")
    @Nullable
    Map<Stat, Integer> cobblenav$getEvYield();
}
