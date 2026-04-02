package com.metacontent.cobblenav.api.pokefinder

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

class AnimatedDotType(
    override val id: ResourceLocation,
    val frames: List<ResourceLocation>,
    val frameDurationTicks: Int
) : RadarDotType {
    override fun getTexture(): ResourceLocation? {
        val frameIndex = Minecraft.getInstance().level?.dayTime?.rem(frameDurationTicks)?.rem(frames.size) ?: 0
        return frames.getOrNull(frameIndex.toInt())
    }
}