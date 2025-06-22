package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.getPlayer
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.metacontent.cobblenav.api.event.CobblenavEvents
import com.metacontent.cobblenav.api.event.profile.TitlesGranted
import com.metacontent.cobblenav.api.event.profile.TitlesRemoved
import com.metacontent.cobblenav.storage.client.ClientProfilePlayerData
import com.metacontent.cobblenav.util.getProfileData
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class ProfilePlayerData(
    override val uuid: UUID,
    var titleId: ResourceLocation?,
    val grantedTitles: MutableSet<ResourceLocation>,
    var partnerPokemonUuid: UUID?,
    var partnerPokemonCache: PokemonProperties?
) : InstancedPlayerData {
    companion object {
        val CODEC: Codec<ProfilePlayerData> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                ResourceLocation.CODEC.optionalFieldOf("titleId").forGetter { Optional.ofNullable(it.titleId) },
                ResourceLocation.CODEC.listOf().fieldOf("grantedTitles").forGetter { it.grantedTitles.toList() },
                PrimitiveCodec.STRING.optionalFieldOf("partnerPokemonUuid")
                    .forGetter { Optional.ofNullable(it.partnerPokemonUuid?.toString()) },
                PokemonProperties.CODEC.optionalFieldOf("partnerPokemonCache")
                    .forGetter { Optional.ofNullable(it.partnerPokemonCache) }
            ).apply(instance) { uuid, titleId, grantedTitles, partnerPokemonUuid, partnerPokemonCache ->
                ProfilePlayerData(
                    uuid = UUID.fromString(uuid),
                    titleId = titleId.getOrNull(),
                    grantedTitles = grantedTitles.toMutableSet(),
                    partnerPokemonUuid = partnerPokemonUuid.getOrNull()?.let { UUID.fromString(it) },
                    partnerPokemonCache = partnerPokemonCache.getOrNull()
                )
            }
        }

        fun executeAndSafe(uuid: UUID, action: (ProfilePlayerData) -> Boolean) {
            val data = Cobblemon.playerDataManager.getProfileData(uuid)
            if (action(data)) {
                Cobblemon.playerDataManager.saveSingle(data, CobblenavDataStoreTypes.PROFILE)
            }
        }

        fun executeAndSafe(player: ServerPlayer, action: (ProfilePlayerData) -> Boolean) {
            executeAndSafe(player.uuid, action)
        }
    }

    private val player: ServerPlayer? by lazy { uuid.getPlayer() }

    fun grantTitle(titleId: ResourceLocation): Boolean {
        if (!grantedTitles.add(titleId)) return false
        CobblenavEvents.TITLES_GRANTED.post(TitlesGranted(player, listOf(titleId)))
        onTitleListUpdated()
        return true
    }

    fun grantTitles(titleIds: List<ResourceLocation>): Boolean {
        val added = titleIds.filter { grantedTitles.add(it) }
        if (added.isEmpty()) return false
        CobblenavEvents.TITLES_GRANTED.post(TitlesGranted(player, added))
        onTitleListUpdated()
        return true
    }

    fun removeTitle(titleId: ResourceLocation): Boolean {
        if (!grantedTitles.remove(titleId)) return false
        CobblenavEvents.TITLES_REMOVED.post(TitlesRemoved(player, listOf(titleId)))
        onTitleListUpdated()
        return true
    }

    fun clearTitles() {
        if (grantedTitles.isEmpty()) return
        val copy = grantedTitles.toList()
        grantedTitles.clear()
        CobblenavEvents.TITLES_REMOVED.post(TitlesRemoved(player, copy))
        onTitleListUpdated()
    }

    private fun onTitleListUpdated() {
        player?.let {
            SetClientPlayerDataPacket(
                type = CobblenavDataStoreTypes.PROFILE,
                playerData = ClientProfilePlayerData(allowedTitles = TrainerTitles.getAllowed(grantedTitles)),
                isIncremental = true
            ).sendToPlayer(it)
        }
    }

    override fun toClientData(): ClientInstancedPlayerData {
        return ClientProfilePlayerData(
            title = titleId?.let { TrainerTitles.getTitle(it) },
            allowedTitles = TrainerTitles.getAllowed(grantedTitles),
            partnerPokemonUuid = partnerPokemonUuid
        )
    }
}