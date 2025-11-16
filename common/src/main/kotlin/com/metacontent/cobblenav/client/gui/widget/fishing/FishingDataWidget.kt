package com.metacontent.cobblenav.client.gui.widget.fishing

import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition
import com.cobblemon.mod.common.entity.PoseType
import com.metacontent.cobblenav.api.platform.BiomePlatformRenderDataRepository
import com.metacontent.cobblenav.api.platform.DimensionPlate
import com.metacontent.cobblenav.api.platform.DimensionPlateRepository
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.SpawnDataTooltipDisplayer
import com.metacontent.cobblenav.client.gui.widget.location.SpawnDataWidget
import com.metacontent.cobblenav.spawndata.SpawnData
import org.joml.Vector3f

class FishingDataWidget(
    x: Int,
    y: Int,
    spawnData: SpawnData,
    displayer: SpawnDataTooltipDisplayer,
    pose: PoseType = if (spawnData.spawningContext == SubmergedSpawningCondition.NAME && CobblenavClient.config.useSwimmingAnimationIfSubmerged) PoseType.SWIM else PoseType.PROFILE,
    pokemonRotation: Vector3f = Vector3f(15F, 35F, 0F),
    chanceMultiplier: Float = 1f
) : SpawnDataWidget(x, y, spawnData, displayer, {}, pose, pokemonRotation, chanceMultiplier) {
    override val platform = BiomePlatformRenderDataRepository.FISHING
    override val plate: DimensionPlate = DimensionPlateRepository.FISHING
}