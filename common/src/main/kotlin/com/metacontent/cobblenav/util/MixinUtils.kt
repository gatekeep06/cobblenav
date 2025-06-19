package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity

fun PokeRodFishingBobberEntity.isTraveling(): Boolean = (this as FishTravelChecker).`cobblenav$isTraveling`()