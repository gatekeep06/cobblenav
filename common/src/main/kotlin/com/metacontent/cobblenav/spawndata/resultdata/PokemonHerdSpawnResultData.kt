package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Environment
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.client.CobblemonClient
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
import com.metacontent.cobblenav.util.getKnowledge
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
    val allPokemon: List<RenderablePokemon>,
    val positionType: String
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? {
            if (detail !is PokemonHerdSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonHerdSpawnDetail.TYPE}).")
                return null
            }

            //TODO: better leader choosing
            val heardables = detail.herdablePokemon.toMutableList()
            if (heardables.isEmpty()) return null

            val leaders = heardables.filter { it.isLeader == true }.ifEmpty { heardables }
            val leader = leaders.random()

            val herd = heardables.filter { it.isLeader != true }.ifEmpty { heardables }.randomNoCopy(2)
            val leftPokemon = herd.getOrNull(0)?.pokemon?.createAndGetAsRenderable(player.serverLevel(), player.onPos)
            val rightPokemon = herd.getOrNull(1)?.pokemon?.createAndGetAsRenderable(player.serverLevel(), player.onPos)

            val allPokemon = detail.herdablePokemon.map {
                it.pokemon.createAndGetAsRenderable(player.serverLevel(), player.onPos)
            }

            if (isUnknown(allPokemon.map { player.pokedex().getKnowledge(it) }) && Cobblenav.config.hideUnknownPokemon)
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
            allPokemon = buffer.readList { RenderablePokemon.loadFromBuffer(it as RegistryFriendlyByteBuf) },
            positionType = buffer.readString()
        )

        fun isUnknown(knowledge: Collection<PokedexEntryProgress>) =
            knowledge.filter { it != PokedexEntryProgress.NONE }.size.toDouble() / knowledge.size < Cobblenav.config.percentageForKnownHerd
    }

    private val pokemonKnowledge: Map<RenderablePokemon, PokedexEntryProgress>
        get() = allPokemon.associateWith { CobblemonClient.clientPokedexData.getKnowledge(it) }

    override val type = PokemonHerdSpawnDetail.TYPE

    override val dataWidgets: List<AbstractWidget>? by lazy {
        val widgets = mutableListOf<AbstractWidget>(
            TextWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailWidget.SECTION_WIDTH - 2,
                text = translate("gui.cobblenav.spawn_data.pokemon_herd").also { component ->
                    pokemonKnowledge.keys.forEachIndexed { index, pokemon ->
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

    override fun drawPortrait(poseStack: PoseStack, x: Float, y: Float, z: Float) {
        leaderRenderer.renderPortrait(leaderPokemon, poseStack, x, y, z)
    }

    override fun encodeResultData(buffer: RegistryFriendlyByteBuf) {
        leaderPokemon.saveToBuffer(buffer)
        buffer.writeNullable(leftPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeNullable(rightPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
        buffer.writeCollection(allPokemon) { buf, pkm -> pkm.saveToBuffer(buf as RegistryFriendlyByteBuf) }
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

    override fun shouldRenderPokeBall() =
        leaderRenderer.shouldRenderPlatform() && pokemonKnowledge.values.all { it == PokedexEntryProgress.CAUGHT }

    override fun getRotation() = leaderRenderer.rotation

    override fun isUnknown() = isUnknown(pokemonKnowledge.values)

    override fun getResultKnowledge(): SpawnResultData.Knowledge {
        val amount = pokemonKnowledge.count { it.value == PokedexEntryProgress.CAUGHT }
        return if (amount == allPokemon.size) {
            SpawnResultData.Knowledge.FULL
        } else if (amount != 0 || pokemonKnowledge.any { it.value == PokedexEntryProgress.ENCOUNTERED }) {
            SpawnResultData.Knowledge.PARTLY
        } else {
            SpawnResultData.Knowledge.NONE
        }
    }
}