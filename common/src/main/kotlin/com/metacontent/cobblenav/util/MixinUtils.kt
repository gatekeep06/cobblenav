package com.metacontent.cobblenav.util

import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

fun ServerPlayer.savedPreferences(): CompoundTag = (this as PreferencesSaver).`cobblenav$getSavedPreferences`()

fun PokeRodFishingBobberEntity.isTraveling(): Boolean = (this as FishTravelChecker).`cobblenav$isTraveling`()