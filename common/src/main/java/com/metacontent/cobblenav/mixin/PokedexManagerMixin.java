package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.metacontent.cobblenav.event.CobblenavEvents;
import com.metacontent.cobblenav.event.PokemonEncounteredEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static com.cobblemon.mod.common.util.PlayerExtensionsKt.getPlayer;

@Mixin(PokedexManager.class)
public abstract class PokedexManagerMixin {
    @Inject(method = "encounter(Lcom/cobblemon/mod/common/pokemon/Pokemon;)V", at = @At("HEAD"), remap = false)
    protected void fireEvent(Pokemon pokemon, CallbackInfo ci) {
        CobblenavEvents.INSTANCE.getPOKEMON_ENCOUNTERED().emit(new PokemonEncounteredEvent(pokemon, getPlayer(getUuid())));
    }

    @Inject(method = "encounter(Lcom/cobblemon/mod/common/pokedex/scanner/PokedexEntityData;)V", at = @At("HEAD"), remap = false)
    protected void fireEvent(PokedexEntityData pokedexEntityData, CallbackInfo ci) {
        CobblenavEvents.INSTANCE.getPOKEMON_ENCOUNTERED().emit(new PokemonEncounteredEvent(pokedexEntityData.getPokemon(), getPlayer(getUuid())));
    }

    @Shadow
    public abstract UUID getUuid();
}
