package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.cobblemon.mod.common.util.pokedex
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.util.createAndGetAsRenderable
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import kotlin.math.PI

class PokemonSpawnResultData(
    val pokemon: RenderablePokemon,
    val originalProperties: PokemonProperties,
    val knowledge: PokedexEntryProgress,
    val positionType: String
) : SpawnResultData {
    companion object {
        fun transform(detail: SpawnDetail, player: ServerPlayer): SpawnResultData? {
            if (detail !is PokemonSpawnDetail) {
                Cobblenav.LOGGER.error("The provided SpawnDetail type (${detail.type}) does not match the key under which it is registered (${PokemonSpawnDetail.TYPE}).")
                return null
            }

            val renderablePokemon = detail.pokemon.createAndGetAsRenderable()
            val positionType = detail.spawnablePositionType.name

            val speciesRecord = player.pokedex().getSpeciesRecord(renderablePokemon.species.resourceIdentifier)
                ?: return UnknownSpawnResultData(positionType)
            val knowledge =
                speciesRecord.getFormRecord(renderablePokemon.form.name)?.knowledge ?: return UnknownSpawnResultData(positionType)
            if (knowledge == PokedexEntryProgress.NONE) return UnknownSpawnResultData(positionType)

            return PokemonSpawnResultData(
                pokemon = renderablePokemon,
                originalProperties = detail.pokemon,
                knowledge = knowledge,
                positionType = positionType
            )
        }

        fun decodeResultData(buffer: RegistryFriendlyByteBuf): PokemonSpawnResultData = PokemonSpawnResultData(
            pokemon = RenderablePokemon.loadFromBuffer(buffer),
            originalProperties = PokemonProperties.parse(buffer.readString()),
            knowledge = buffer.readEnum(PokedexEntryProgress::class.java),
            positionType = buffer.readString()
        )
    }

    override val type = PokemonSpawnDetail.TYPE

    val renderer: PokemonSpawnResultRenderer by lazy {
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
}

abstract class PokemonSpawnResultRenderer {
    val state = FloatingState()
    abstract val rotation: Vector3f
    abstract val pose: PoseType

    open fun render(pokemon: RenderablePokemon, poseStack: PoseStack, x: Float, y: Float, z: Float, delta: Float) {
        try {
            drawPokemon(
                poseStack = poseStack,
                pokemon = pokemon,
                x = x,
                y = y,
                z = z,
                delta = delta,
                state = state,
                poseType = pose,
                rotation = Quaternionf().fromEulerXYZDegrees(rotation)
            )
        } catch (e: Exception) {
            val message = Component.translatable(
                "gui.cobblenav.pokemon_rendering_exception",
                pokemon.species.translatedName.string,
                pokemon.species.translatedName.string
            )
            Cobblenav.LOGGER.error(message.string)
            Cobblenav.LOGGER.error(e.message)
            if (CobblenavClient.config.sendErrorMessagesToChat) {
                Minecraft.getInstance().player?.sendSystemMessage(message.red())
            }
            throw e
        }
    }

    abstract fun shouldRenderPlatform(): Boolean

    abstract fun shouldRenderPokeBall(): Boolean
}

class BasicSpawnResultRenderer : PokemonSpawnResultRenderer() {
    override val rotation = Vector3f(13F, 35F, 0F)
    override val pose = PoseType.PROFILE

    override fun shouldRenderPlatform() = true

    override fun shouldRenderPokeBall() = true
}

class FishingSpawnResultRenderer : PokemonSpawnResultRenderer() {
    override val rotation = Vector3f(0f, 270f, 0f)
    override val pose = PoseType.SWIM

    override fun shouldRenderPlatform() = false

    override fun shouldRenderPokeBall() = false
}