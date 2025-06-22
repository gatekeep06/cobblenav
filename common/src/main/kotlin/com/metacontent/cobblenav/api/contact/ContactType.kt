package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.Cobblemon
import com.metacontent.cobblenav.util.getProfileData
import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable
import java.util.UUID

enum class ContactType(val profileDataExtractor: (String) -> ContactProfileData) : StringRepresentable{
    PLAYER({ id ->
        val profile = Cobblemon.playerDataManager.getProfileData(UUID.fromString(id))
        ContactProfileData(profile.titleId, profile.partnerPokemonCache)
    }),
    NPC({ id ->
        ContactProfileData(null, null)
    });

    override fun getSerializedName() = this.name

    companion object {
        val CODEC: Codec<ContactType> = StringRepresentable.fromEnum(ContactType::values)
    }
}