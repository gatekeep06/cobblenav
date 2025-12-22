package com.metacontent.cobblenav

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.api.scheduling.ScheduledTask
import com.cobblemon.mod.common.api.scheduling.ServerTaskTracker
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.storage.player.factory.CachedPlayerDataStoreFactory
import com.cobblemon.mod.common.data.CobblemonDataProvider
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.metacontent.cobblenav.api.platform.BiomePlatforms
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.config.Config
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import com.metacontent.cobblenav.networking.packet.client.LabelSyncPacket
import com.metacontent.cobblenav.properties.BucketPropertyType
import com.metacontent.cobblenav.properties.SpawnDetailIdPropertyType
import com.metacontent.cobblenav.spawndata.PokenavSpawnablePositionResolver
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.spawndata.resultdata.PokemonHerdSpawnResultData
import com.metacontent.cobblenav.spawndata.resultdata.PokemonSpawnResultData
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import com.metacontent.cobblenav.spawndata.resultdata.UnknownSpawnResultData
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.adapter.SpawnDataCatalogueNbtBackend
import net.minecraft.world.entity.npc.VillagerTrades
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var config: CobblenavConfig
    lateinit var implementation: Implementation
    val resolver = PokenavSpawnablePositionResolver()

    fun init(implementation: Implementation) {
        config = Config.load(CobblenavConfig::class.java)
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()
        implementation.injectLootTables()

        CobblemonDataProvider.register(BiomePlatforms, true)

        CobblenavEvents.FISH_TRAVEL_STARTED.subscribe { event ->
            CloseFishingnavPacket().sendToPlayer(event.player)
        }

        if (config.syncLabelsWithClient) {
            CobblemonEvents.DATA_SYNCHRONIZED.subscribe { player ->
                LabelSyncPacket(PokemonSpecies.species.map { it.resourceIdentifier to it.labels }).sendToPlayer(player)
            }
        }

        PlatformEvents.SERVER_STARTING.subscribe {
            ConditionCollectors.init()
        }

        PlatformEvents.SERVER_STARTED.subscribe { (server) ->
            val spawnDataNbtFactory = CachedPlayerDataStoreFactory(SpawnDataCatalogueNbtBackend())
            spawnDataNbtFactory.setup(server)

            val manager = Cobblemon.playerDataManager
            manager.setFactory(spawnDataNbtFactory, CobblenavDataStoreTypes.SPAWN_DATA)
            manager.saveTasks[CobblenavDataStoreTypes.SPAWN_DATA] = ScheduledTask.Builder()
                .execute { manager.saveAllOfOneType(CobblenavDataStoreTypes.SPAWN_DATA) }
                .delay(30f)
                .interval(120f)
                .infiniteIterations()
                .tracker(ServerTaskTracker)
                .build()
        }

        SpawnResultData.register(PokemonSpawnDetail.TYPE, PokemonSpawnResultData::transform, PokemonSpawnResultData::decodeResultData)
        SpawnResultData.register(PokemonHerdSpawnDetail.TYPE, PokemonHerdSpawnResultData::transform, PokemonHerdSpawnResultData::decodeResultData)
        SpawnResultData.register(UnknownSpawnResultData.TYPE, UnknownSpawnResultData::transform, UnknownSpawnResultData::decodeResultData)

        SpawnDataHelper.onInit()

        registerCustomProperties()
    }

    private fun registerArgumentTypes() {
    }

    fun resolveWandererTrades() = listOf(
        VillagerTrades.ItemsForEmeralds(CobblenavItems.WANDERER_POKENAV, 24, 1, 1, 60)
    )

    fun registerCustomProperties() {
        registerProperty(SpawnDetailIdPropertyType)
        registerProperty(BucketPropertyType)
    }

    fun registerProperty(property: CustomPokemonPropertyType<*>) {
        CustomPokemonProperty.properties.add(property)
    }
}