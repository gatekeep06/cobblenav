package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.metacontent.cobblenav.properties.BucketSpeciesFeatureProvider;
import com.metacontent.cobblenav.properties.SpawnDetailIdPropertyType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonSpawnAction.class)
public abstract class PokemonSpawnActionMixin {
    @Inject(method = "createEntity()Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;", at = @At("TAIL"), remap = false)
    protected void saveSpawnData(CallbackInfoReturnable<PokemonEntity> cir) {
        SpawnDetail detail = getDetail();
        PokemonEntity pokemon = cir.getReturnValue();
        SpawnDetailIdPropertyType.apply(pokemon, detail.getId());
        new StringSpeciesFeature("spawn_bucket", detail.getBucket().name).apply(pokemon);
    }

    @Shadow
    public abstract SpawnDetail getDetail();
}