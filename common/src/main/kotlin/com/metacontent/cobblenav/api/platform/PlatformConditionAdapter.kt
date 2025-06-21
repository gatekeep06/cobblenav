package com.metacontent.cobblenav.api.platform

import com.cobblemon.mod.common.util.asResource
import com.google.gson.*
import com.metacontent.cobblenav.util.combinations
import net.minecraft.resources.ResourceLocation
import java.lang.reflect.Type

object PlatformConditionAdapter : JsonDeserializer<HashSet<PlatformCondition>> {
//    private const val TAG_PREFIX = "#"

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): HashSet<PlatformCondition> {
        val jsonObject = json.asJsonObject
        val ids = jsonObject.getAsJsonArray("detailIds")?.map { it.asString }
            ?.ifEmpty { listOf(null) } ?: listOf(null)
        val biomes = jsonObject.getAsJsonArray("biomes")?.map { it.asString.asResource() }
            ?.ifEmpty { listOf(null) } ?: listOf(null)
//        val biomes = jsonObject.getAsJsonArray("biomes").flatMap { jsonElement ->
//            val biomeString = jsonElement.asString
//            val biomes = mutableSetOf<ResourceLocation>()
//            val id: ResourceLocation
//            if (biomeString.startsWith(TAG_PREFIX)) {
//                id = biomeString.substring(1).asResource()
//                val biomeTag = TagKey.create(Registries.BIOME, id)
//                return@flatMap (Cobblemon.implementation.server()
//                    ?.registryAccess()
//                    ?.registry(Registries.BIOME)
//                    ?.getOrNull()
//                    ?.getTag(biomeTag)
//                    ?.getOrNull()
//                    ?.mapNotNull { holder ->
//                        holder.unwrapKey().getOrNull()?.location()
//                    } ?: emptyList())
//            }
//            else {
//                id = biomeString.asResource()
//            }
//            return@flatMap biomes.also { it.add(id) }
//        }
        val structures = jsonObject.getAsJsonArray("structures")?.map { it.asString.asResource() }
            ?.ifEmpty { listOf(null) } ?: listOf(null)
        val fluids = jsonObject.getAsJsonArray("fluids")?.map { it.asString.asResource() }
            ?.ifEmpty { listOf(null) } ?: listOf(null)

        return combinations(ids, biomes, structures, fluids).map {
            PlatformCondition(
                id = it[0] as? String?,
                biome = it[1] as? ResourceLocation?,
                structure = it[2] as? ResourceLocation?,
                fluid = it[3] as? ResourceLocation?
            )
        }.toHashSet()
    }
}