package com.metacontent.cobblenav.spawndata.resultdata

import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.util.drawPokemon
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.joml.Quaternionf
import org.joml.Vector3f

abstract class PokemonSpawnResultRenderer {
    val state = FloatingState()
    abstract val rotation: Vector3f
    abstract val scale: Float
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
                scale = scale,
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

    abstract fun getHerdRenderer(): PokemonSpawnResultRenderer
}

open class BasicSpawnResultRenderer : PokemonSpawnResultRenderer() {
    override val rotation = Vector3f(13F, 35F, 0F)
    override val scale = 15f
    override val pose = PoseType.PROFILE

    override fun shouldRenderPlatform() = true

    override fun shouldRenderPokeBall() = true

    override fun getHerdRenderer() = BasicHerdSpawnResultRenderer()
}

class BasicHerdSpawnResultRenderer : BasicSpawnResultRenderer() {
    override val scale = 10f
}

open class FishingSpawnResultRenderer : PokemonSpawnResultRenderer() {

    override val rotation = Vector3f(0f, 270f, 0f)
    override val scale = 15f
    override val pose = PoseType.SWIM

    override fun shouldRenderPlatform() = false

    override fun shouldRenderPokeBall() = false

    override fun getHerdRenderer() = FishingHerdSpawnResultRenderer()
}

class FishingHerdSpawnResultRenderer : FishingSpawnResultRenderer() {
    override val scale = 10f
}