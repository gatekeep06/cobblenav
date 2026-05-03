package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreManager
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import net.minecraft.server.level.ServerPlayer
import java.util.*

fun PlayerInstancedDataStoreManager.getSpawnDataCatalogue(playerId: UUID): SpawnDataCatalogue =
    get(playerId, CobblenavDataStoreTypes.SPAWN_DATA) as SpawnDataCatalogue

fun PlayerInstancedDataStoreManager.getSpawnDataCatalogue(player: ServerPlayer): SpawnDataCatalogue =
    getSpawnDataCatalogue(player.uuid)

fun ServerPlayer.spawnCatalogue(): SpawnDataCatalogue = Cobblemon.playerDataManager.getSpawnDataCatalogue(this)

fun AbstractPokedexManager.getKnowledge(pokemon: RenderablePokemon): PokedexEntryProgress =
    this.getSpeciesRecord(pokemon.species.resourceIdentifier)
        ?.getFormRecord(pokemon.form.name)?.knowledge ?: PokedexEntryProgress.NONE