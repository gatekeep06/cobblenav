package com.metacontent.cobblenav.api.contact.type

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.networking.packet.client.NPCContactTypeRegistrySyncPacket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType

object NPCContactTypes : JsonDataRegistry<NPCContactType> {
    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .setPrettyPrinting()
        .create()
    override val id = cobblenavResource("contact_types")
    override val observable = SimpleObservable<NPCContactTypes>()
    override val resourcePath = "contact_types"
    override val type = PackType.SERVER_DATA
    override val typeToken: TypeToken<NPCContactType> = TypeToken.get(NPCContactType::class.java)

    private val types = mutableMapOf<ResourceLocation, NPCContactType>()

    fun getType(id: ResourceLocation): NPCContactType? = types[id]

    fun getType(npcEntity: NPCEntity): NPCContactType? = getType(npcEntity.npc.id)

    override fun sync(player: ServerPlayer) {
        NPCContactTypeRegistrySyncPacket(types.values).sendToPlayer(player)
    }

    override fun reload(data: Map<ResourceLocation, NPCContactType>) {
        data.forEach {
            it.value.npcClass = it.key
            types[it.key] = it.value
        }
        observable.emit(this)
    }
}