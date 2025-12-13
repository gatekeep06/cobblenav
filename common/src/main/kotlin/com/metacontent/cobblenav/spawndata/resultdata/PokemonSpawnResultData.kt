package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.*
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.widget.TextWidget
import com.metacontent.cobblenav.client.gui.widget.section.SectionWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailWidget
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

class PokemonSpawnResultData(
    val pokemon: RenderablePokemon,
    val originalProperties: PokemonProperties,
    val level: IntRange?,
    val drops: Map<ResourceLocation, Float>?,
    val heldItems: Map<ResourceLocation, Float>?,
    val knowledge: PokedexEntryProgress,
    val positionType: String
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? {
            if (detail !is PokemonSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonSpawnDetail.TYPE}).")
                return null
            }

            val renderablePokemon = detail.pokemon.createAndGetAsRenderable(player.serverLevel(), player.onPos)
            val positionType = detail.spawnablePositionType.name

            val knowledge = player.pokedex()
                .getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
                ?.getFormRecord(renderablePokemon.form.name)?.knowledge ?: PokedexEntryProgress.NONE
            if (knowledge == PokedexEntryProgress.NONE && Cobblenav.config.hideUnknownSpawns) return UnknownSpawnResultData(
                positionType
            )

            return PokemonSpawnResultData(
                pokemon = renderablePokemon,
                originalProperties = detail.pokemon,
                level = detail.levelRange,
                drops = detail.drops?.entries?.filterIsInstance<ItemDropEntry>()?.associate {
                    it.item to it.percentage
                },
                heldItems = detail.heldItems?.associate {
                    ResourceLocation.parse(it.item) to it.percentage.toFloat()
                },
                knowledge = knowledge,
                positionType = positionType
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonSpawnResultData = PokemonSpawnResultData(
            pokemon = RenderablePokemon.loadFromBuffer(buffer),
            originalProperties = PokemonProperties.parse(buffer.readString()),
            level = buffer.readNullable { it.readVarInt()..it.readVarInt() },
            drops = buffer.readNullable { buf ->
                buf.readMap(
                    { it.readIdentifier() },
                    { it.readFloat() }
                )
            },
            heldItems = buffer.readNullable { buf ->
                buf.readMap(
                    { it.readIdentifier() },
                    { it.readFloat() }
                )
            },
            knowledge = buffer.readEnum(PokedexEntryProgress::class.java),
            positionType = buffer.readString()
        )
    }

    override val type = PokemonSpawnDetail.TYPE

    override val dataWidgets: List<AbstractWidget>? by lazy {
        val widgets = mutableListOf<AbstractWidget>(
            TextWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailWidget.SECTION_WIDTH - 2,
                text = Component.translatable("gui.cobblenav.spawn_data.pokemon")
                    .append(pokemon.species.translatedName)
            )
        )
        level?.let {
            widgets.add(
                TextWidget(
                    x = 0,
                    y = 0,
                    width = SpawnDataDetailWidget.SECTION_WIDTH - 2,
                    text = Component.translatable("gui.cobblenav.spawn_data.level", "${it.first} - ${it.last}")
                )
            )
        }
        listOf(
            SectionWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailWidget.SECTION_WIDTH,
                title = Component.translatable("gui.cobblenav.spawn_data.title.result"),
                widgets = widgets,
                color = RGB(144, 213, 255)
            )
        )
    }

    private val renderer: PokemonSpawnResultRenderer by lazy {
        when (positionType) {
            "fishing" -> FishingSpawnResultRenderer()
            else -> BasicSpawnResultRenderer()
        }
    }

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        renderer.render(pokemon, poseStack, x, y, z, delta)
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        pokemon.saveToBuffer(buffer)
        buffer.writeString(originalProperties.asString())
        buffer.writeNullable(level) { buf, l ->
            buf.writeVarInt(l.first)
            buf.writeVarInt(l.last)
        }
        buffer.writeNullable(drops) { buf, map ->
            buf.writeMap(
                map,
                { b, rl -> b.writeIdentifier(rl) },
                { b, f -> b.writeFloat(f) }
            )
        }
        buffer.writeNullable(heldItems) { buf, map ->
            buf.writeMap(
                map,
                { b, rl -> b.writeIdentifier(rl) },
                { b, f -> b.writeFloat(f) }
            )
        }
        buffer.writeEnum(knowledge)
        buffer.writeString(positionType)
    }

    override fun canBeTracked() = true

    override fun containsResult(objects: Collection<*>) = objects.contains(pokemon.form.showdownId())

    override fun getColor() = pokemon.form.primaryType.hue

    override fun getResultName(): MutableComponent = pokemon.species.translatedName

    override fun shouldRenderPlatform() = renderer.shouldRenderPlatform()

    override fun shouldRenderPokeBall() = renderer.shouldRenderPokeBall() && knowledge == PokedexEntryProgress.CAUGHT

    override fun getRotation() = renderer.rotation

    override fun isUnknown() = knowledge == PokedexEntryProgress.NONE
}