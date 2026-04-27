package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.pokedex
import com.cobblemon.mod.common.util.randomNoCopy
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.client.gui.widget.TextWidget
import com.metacontent.cobblenav.client.gui.widget.section.SectionWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailWidget
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class PokemonHerdSpawnResultData(
    val leftPokemon: RenderablePokemon?,
    val leaderPokemon: RenderablePokemon,
    val rightPokemon: RenderablePokemon?,
    val allPokemon: Map<RenderablePokemon, PokedexEntryProgress>,
    val positionType: String
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? {
            if (detail !is PokemonHerdSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonHerdSpawnDetail.TYPE}).")
                return null
            }

            val heardables = detail.herdablePokemon.toMutableList()
            if (heardables.isEmpty()) return null

            val leaders = heardables.filter { it.isLeader == true }.ifEmpty { heardables }
            val leader = leaders.random()

            val herd = heardables.filter { it.isLeader != true }.ifEmpty { heardables }.randomNoCopy(2)
            val leftPokemon = herd.getOrNull(0)?.pokemon?.createAndGetAsRenderable(player.serverLevel(), player.onPos)
            val rightPokemon = herd.getOrNull(1)?.pokemon?.createAndGetAsRenderable(player.serverLevel(), player.onPos)

            val pokedex = player.pokedex()
            val allPokemon = detail.herdablePokemon.associate {
                val pokemon = it.pokemon.createAndGetAsRenderable(player.serverLevel(), player.onPos)
                val knowledge = pokedex
                    .getSpeciesRecord(pokemon.species.resourceIdentifier)
                    ?.getFormRecord(pokemon.form.name)?.knowledge ?: PokedexEntryProgress.UNREGISTERED
                pokemon to knowledge
            }

            if (isUnknown(allPokemon.values) && Cobblenav.config.hideUnknownPokemon)
                return UnknownSpawnResultData(detail.spawnablePositionType.name)

            return PokemonHerdSpawnResultData(
                leftPokemon = leftPokemon,
                leaderPokemon = leader.pokemon.createAndGetAsRenderable(player.serverLevel(), player.onPos),
                rightPokemon = rightPokemon,
                allPokemon = allPokemon,
                positionType = detail.spawnablePositionType.name
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonHerdSpawnResultData = PokemonHerdSpawnResultData(
            leaderPokemon = RenderablePokemon.loadFromBuffer(buffer),
            leftPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            rightPokemon = buffer.readNullable { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            allPokemon = buffer.readMap(
                { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
                { it.readEnum(PokedexEntryProgress::class.java) }
            ),
            positionType = buffer.readString()
        )

        fun isUnknown(knowledge: Collection<PokedexEntryProgress>) =
            knowledge.filter { it != PokedexEntryProgress.UNREGISTERED }.size.toDouble() / knowledge.size < Cobblenav.config.percentageForKnownHerd
    }

    override val type = PokemonHerdSpawnDetail.TYPE

    override val dataWidgets: List<AbstractWidget>? by lazy {
        val widgets = mutableListOf<AbstractWidget>(
            TextWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailWidget.SECTION_WIDTH - 2,
                text = translate("gui.cobblenav.spawn_data.pokemon_herd").also { component ->
                    allPokemon.keys.forEachIndexed { index, pokemon ->
                        component.append(pokemon.species.translatedName)
                        if (index < allPokemon.size - 1) {
                            component.append(", ")
                        }
                    }
                }
            )
        )
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

    private val leaderRenderer: PokemonSpawnResultRenderer by lazy {
        when (positionType) {
            "fishing" -> FishingSpawnResultRenderer()
            else -> BasicSpawnResultRenderer()
        }
    }
    private val herdRenderer: PokemonSpawnResultRenderer by lazy {
        leaderRenderer.getHerdRenderer()
    }

    override fun drawResult(poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        leaderRenderer.render(leaderPokemon, poseStack, x, y + 3, z + 100, delta)
        leftPokemon?.let { herdRenderer.render(it, poseStack, x - 10, y + 3, z - 100, delta) }
        rightPokemon?.let { herdRenderer.render(it, poseStack, x + 10, y + 3, z - 100, delta) }
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        leaderPokemon.saveToBuffer(buffer)
        buffer.writeNullable(leftPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeNullable(rightPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeMap(
            allPokemon,
            { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) },
            { buf, knowledge -> buf.writeEnum(knowledge) }
        )
        buffer.writeString(positionType)
    }

    override fun getResultPokemon(): PokemonProperties? = null

    override fun getResultId(): String? = null

    override fun canBeTracked() = false

    override fun containsResult(objects: Collection<*>) = false

    override fun getColor() = leaderPokemon.form.primaryType.hue

    override fun getResultName(): MutableComponent = Component.translatable(
        "gui.cobblenav.spawn_data.herd",
        leaderPokemon.species.translatedName.string
    )

    override fun shouldRenderPlatform() = leaderRenderer.shouldRenderPlatform()

    override fun shouldRenderPokeBall() = leaderRenderer.shouldRenderPlatform() && allPokemon.values.all { it == PokedexEntryProgress.OWNED }

    override fun getRotation() = leaderRenderer.rotation

    override fun isUnknown() = isUnknown(allPokemon.values)
}