package com.metacontent.cobblenav

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.scheduling.ScheduledTask
import com.cobblemon.mod.common.api.scheduling.ServerTaskTracker
import com.cobblemon.mod.common.api.storage.player.factory.CachedPlayerDataStoreFactory
import com.cobblemon.mod.common.data.CobblemonDataProvider
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.metacontent.cobblenav.config.CobblenavConfig
import com.metacontent.cobblenav.config.Config
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.networking.packet.client.CloseFishingnavPacket
import com.metacontent.cobblenav.networking.packet.client.LabelSyncPacket
import com.metacontent.cobblenav.spawndata.collector.ConditionCollectors
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import com.metacontent.cobblenav.storage.adapter.ContactDataNbtBackend
import com.metacontent.cobblenav.storage.adapter.ProfileDataNbtBackend
import com.metacontent.cobblenav.util.PokenavSpawningProspector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Cobblenav {
    const val ID = "cobblenav"
    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    lateinit var config: CobblenavConfig
    lateinit var implementation: Implementation
    val prospector = PokenavSpawningProspector

    fun init(implementation: Implementation) {
        config = Config.load(CobblenavConfig::class.java)
        this.implementation = implementation
        implementation.registerItems()
        registerArgumentTypes()
        implementation.registerCommands()
        implementation.injectLootTables()

        ConditionCollectors.init()

        CobblemonDataProvider.register(TrainerTitles)

        PlatformEvents.SERVER_STARTED.subscribe { event ->
            val profileNbtFactory = CachedPlayerDataStoreFactory(ProfileDataNbtBackend())
            profileNbtFactory.setup(event.server)

            val contactNbtFactory = CachedPlayerDataStoreFactory(ContactDataNbtBackend())
            contactNbtFactory.setup(event.server)

            val manager = Cobblemon.playerDataManager
            manager.setFactory(profileNbtFactory, CobblenavDataStoreTypes.PROFILE)
            manager.setFactory(contactNbtFactory, CobblenavDataStoreTypes.CONTACTS)
            manager.saveTasks[CobblenavDataStoreTypes.PROFILE] = ScheduledTask.Builder()
                .execute { manager.saveAllOfOneType(CobblenavDataStoreTypes.PROFILE) }
                .delay(30f)
                .interval(120f)
                .infiniteIterations()
                .tracker(ServerTaskTracker)
                .build()
            manager.saveTasks[CobblenavDataStoreTypes.CONTACTS] = ScheduledTask.Builder()
                .execute { manager.saveAllOfOneType(CobblenavDataStoreTypes.CONTACTS) }
                .delay(30f)
                .interval(120f)
                .infiniteIterations()
                .tracker(ServerTaskTracker)
                .build()
        }

        CobblenavEvents.FISH_TRAVEL_STARTED.subscribe { event ->
            CloseFishingnavPacket().sendToPlayer(event.player)
        }
      
        if (config.syncLabelsWithClient) {
            CobblemonEvents.DATA_SYNCHRONIZED.subscribe { player ->
                LabelSyncPacket(PokemonSpecies.species.map { it.resourceIdentifier to it.labels }).sendToPlayer(player)
            }
        }
    }

    private fun registerArgumentTypes() {
    }
}