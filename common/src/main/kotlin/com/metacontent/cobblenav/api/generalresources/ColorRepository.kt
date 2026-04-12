package com.metacontent.cobblenav.api.generalresources

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.PackType
import net.minecraft.util.FastColor

object ColorRepository : JsonDataRegistry<ColorRepository.Colors> {
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
    override val id = cobblenavResource("colors")
    override val observable = SimpleObservable<ColorRepository>()
    override val resourcePath = "cobblenav_colors"
    override val type = PackType.CLIENT_RESOURCES
    override val typeToken: TypeToken<Colors> = TypeToken.get(Colors::class.java)

    private val colors = hashMapOf<String, Int>()

    fun get(key: String): Int = colors[key]?: 0xffffff

    override fun sync(player: ServerPlayer) {}

    override fun reload(data: Map<ResourceLocation, Colors>) {
        colors.clear()
        registerDefaultColors()
        data.forEach {
            colors.putAll(it.value.mapping)
        }
    }

    fun registerDefaultColors() {
        default("pokefinder_text", 1, 235, 95)
        default("pokefinder_background", 37, 52, 47)
    }

    private fun default(key: String, value: Int) {
        colors[key] = value
    }

    private fun default(key: String, r: Int, g: Int, b: Int, a: Int = 255) {
        default(key, FastColor.ARGB32.color(a, r, g, b))
    }

    data class Colors(
        val mapping: Map<String, Int>
    )
}