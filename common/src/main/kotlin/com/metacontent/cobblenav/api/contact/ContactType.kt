package com.metacontent.cobblenav.api.contact

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.util.asResource
import com.metacontent.cobblenav.api.contact.npc.NPCProfiles
import com.metacontent.cobblenav.util.getProfileData
import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable
import java.util.*

enum class ContactType(val profileDataExtractor: (String) -> ContactProfileData) : StringRepresentable {
    PLAYER({ id ->
        val profile = Cobblemon.playerDataManager.getProfileData(UUID.fromString(id))
        ContactProfileData(profile.titleId, profile.partnerPokemonCache)
    }),
    NPC({ id ->
        val profile = NPCProfiles.get(id.asResource())
        ContactProfileData(profile?.title, profile?.partnerPokemon)
    });

    override fun getSerializedName() = this.name

    companion object {
        val CODEC: Codec<ContactType> = StringRepresentable.fromEnum(ContactType::values)
    }
}