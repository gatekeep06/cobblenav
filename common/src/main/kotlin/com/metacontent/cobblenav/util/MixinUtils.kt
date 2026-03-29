package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.api.pokemon.feature.GlobalSpeciesFeatures
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.pokemon.FormData
import com.metacontent.cobblenav.mixin.FormDataMixin
import com.metacontent.cobblenav.mixin.GrowingPlantBlockMixin
import net.minecraft.world.level.block.GrowingPlantBlock
import net.minecraft.world.level.block.GrowingPlantHeadBlock

fun PokeRodFishingBobberEntity.isTraveling(): Boolean = (this as FishTravelChecker).`cobblenav$isTraveling`()

fun GrowingPlantBlock.getHeadBlock(): GrowingPlantHeadBlock = (this as GrowingPlantBlockMixin).invokeGetHeadBlock()

fun GlobalSpeciesFeatures.registerDirectly(name: String, provider: SpeciesFeatureProvider<*>) =
    (this as DirectFeatureRegistrar).`cobblenav$registerDirectly`(name, provider)

fun FormData.setEvYield(evYield: MutableMap<Stat, Int>?) = (this as FormDataMixin).`cobblenav$setEvYield`(evYield)

fun FormData.getEvYield(): Map<Stat, Int>? = (this as FormDataMixin).`cobblenav$getEvYield`()