package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.metacontent.cobblenav.mixin.GrowingPlantBlockMixin
import net.minecraft.world.level.block.GrowingPlantBlock
import net.minecraft.world.level.block.GrowingPlantHeadBlock

fun PokeRodFishingBobberEntity.isTraveling(): Boolean = (this as FishTravelChecker).`cobblenav$isTraveling`()

fun GrowingPlantBlock.getHeadBlock(): GrowingPlantHeadBlock = (this as GrowingPlantBlockMixin).invokeGetHeadBlock()