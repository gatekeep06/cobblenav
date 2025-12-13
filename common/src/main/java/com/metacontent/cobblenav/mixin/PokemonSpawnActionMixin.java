package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.spawning.SpawnBucket;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.metacontent.cobblenav.properties.BucketPropertyType;
import com.metacontent.cobblenav.properties.SpawnDetailIdPropertyType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonSpawnAction.class)
public abstract class PokemonSpawnActionMixin extends SpawnAction<PokemonEntity> {
    public PokemonSpawnActionMixin(@NotNull SpawnablePosition spawnablePosition, @NotNull SpawnBucket bucket, @NotNull SpawnDetail detail) {
        super(spawnablePosition, bucket, detail);
    }

    @Inject(method = "createEntity()Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;", at = @At("TAIL"), remap = false)
    protected void saveSpawnData(CallbackInfoReturnable<PokemonEntity> cir) {
        SpawnDetail detail = getDetail();
        PokemonEntity pokemon = cir.getReturnValue();
        SpawnDetailIdPropertyType.apply(pokemon, detail.getId());
        BucketPropertyType.apply(pokemon, detail.getBucket().name);
    }
}