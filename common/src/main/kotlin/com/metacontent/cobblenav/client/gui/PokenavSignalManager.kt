package com.metacontent.cobblenav.client.gui

import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.cobblemon.mod.common.util.removeIf
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.item.Pokenav
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.sin

object PokenavSignalManager {
    const val SIGNAL_ITEM_SCALE = 1.025f
    const val SHAKE_AMPLITUDE = 4f
    const val BASE_SHAKE_FREQUENCY = 16f
    const val BASE_SHAKE_DURATION = 10f
    const val WAIT_DURATION = 10f

    val SPAWN_CATALOGUED_SIGNAL = Signal(itemClass = Pokenav::class.java, duration = 10f)
    val POKEMON_APPEARED_SIGNAL = Signal(itemClass = Pokefinder::class.java, duration = 5f)

    private val queues = mutableMapOf<Class<*>, ArrayDeque<Signal>>()

    private var currentSignals = mutableMapOf<Class<*>, Signal>()

    @JvmStatic
    fun tick(delta: Float) {
        currentSignals.removeIf { (_, signal) -> signal.isOver() }
        currentSignals.forEach { (_, signal) -> signal.tick(delta) }

        queues.forEach { (itemClass, queue) ->
            if (queue.isNotEmpty() && !currentSignals.contains(itemClass)) {
                currentSignals[itemClass] = queue.removeFirst()
            }
        }
    }

    fun add(signal: Signal) {
        queues.getOrPut(signal.itemClass) { ArrayDeque() }.add(signal)
    }

    @JvmStatic
    fun hasSignal(stack: ItemStack) = currentSignals[stack.item::class.java] != null

    @JvmStatic
    fun getSignal(stack: ItemStack) = currentSignals[stack.item::class.java]

    data class Signal(
        val itemClass: Class<*>,
        val duration: Float,
        val waitDuration: Float = WAIT_DURATION
    ) {
        private val timer = Timer(duration)

        private fun getRotation() = Quaternionf().fromEulerXYZDegrees(
            Vector3f(
                0f,
                0f,
                SHAKE_AMPLITUDE * sin(timer.getProgress() * PI.toFloat() / 2 * (BASE_SHAKE_FREQUENCY * duration / BASE_SHAKE_DURATION))
            )
        )

        fun shake(poseStack: PoseStack) {
            poseStack.scale(SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE)
            poseStack.rotateAround(getRotation(), 0f, -0.25f, 0f)
        }

        fun tick(delta: Float) {
            timer.tick(delta)
        }

        fun isOver() = timer.isOver()
    }
}