package com.metacontent.cobblenav.api.contact.npc

import com.cobblemon.mod.common.api.dialogue.Dialogue
import com.cobblemon.mod.common.api.dialogue.DialogueManager
import com.cobblemon.mod.common.api.dialogue.Dialogues
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.metacontent.cobblenav.api.contact.PokenavContact
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

interface PostBattleContactProvider {
    fun canShare(): Boolean

    fun provide(player: ServerPlayer, entity: NPCEntity, contact: PokenavContact): PokenavContact?
}

class NoContactProvider : PostBattleContactProvider {
    override fun canShare() = false

    override fun provide(player: ServerPlayer, entity: NPCEntity, contact: PokenavContact): PokenavContact? = null
}

class ImmediateContactProvider : PostBattleContactProvider {
    override fun canShare() = true

    override fun provide(player: ServerPlayer, entity: NPCEntity, contact: PokenavContact): PokenavContact = contact
}

class BattleRecordProvider : PostBattleContactProvider {
    override fun canShare() = true

    override fun provide(player: ServerPlayer, entity: NPCEntity, contact: PokenavContact): PokenavContact? = null
}

class DialogueContactProvider(
    val dialogueId: ResourceLocation
) : PostBattleContactProvider {
    val dialogue: Dialogue? by lazy { Dialogues.dialogues[dialogueId] }

    override fun canShare() = dialogue != null

    override fun provide(player: ServerPlayer, entity: NPCEntity, contact: PokenavContact): PokenavContact? {
        dialogue?.let { DialogueManager.startDialogue(player, entity, it) }
        return null
    }
}