package com.metacontent.cobblenav.api.contact.npc

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.PokemonPropertiesAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object NPCProfiles : JsonDataRegistry<NPCProfile> {
    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, PokemonPropertiesAdapter(false))
        .registerTypeAdapter(PostBattleContactProvider::class.java, PostBattleContactProviderAdapter)
        .setPrettyPrinting()
        .create()
    override val id = cobblenavResource("npc_profiles")
    override val observable = SimpleObservable<NPCProfiles>()
    override val resourcePath = "npc_profiles"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<NPCProfile> = TypeToken.get(NPCProfile::class.java)

    private val profiles = hashMapOf<ResourceLocation, NPCProfile>()

    fun get(id: ResourceLocation) = profiles[id]

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, NPCProfile>) {
        data.forEach { (_, profile) ->
            profiles[profile.id] = profile
        }
        Cobblenav.LOGGER.info("Loaded {} npc profiles", profiles.size)
    }
}