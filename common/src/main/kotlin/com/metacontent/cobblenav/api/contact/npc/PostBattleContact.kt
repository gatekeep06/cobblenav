package com.metacontent.cobblenav.api.contact.npc

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.dialogue.Dialogue
import com.cobblemon.mod.common.api.dialogue.DialogueManager
import com.cobblemon.mod.common.api.dialogue.Dialogues
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.entity.npc.NPCBattleActor
import com.metacontent.cobblenav.api.contact.PokenavContact
import net.minecraft.resources.ResourceLocation

interface PostBattleContactProvider {
    fun canShare(
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): Boolean

    fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact?
}

class NoContactProvider : PostBattleContactProvider {
    override fun canShare(playerActor: PlayerBattleActor, npcActor: NPCBattleActor, winners: List<BattleActor>) = false

    override fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact? = null
}

class ImmediateContactProvider : PostBattleContactProvider {
    override fun canShare(playerActor: PlayerBattleActor, npcActor: NPCBattleActor, winners: List<BattleActor>) = true

    override fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact = contact
}

class BattleRecordProvider : PostBattleContactProvider {
    override fun canShare(playerActor: PlayerBattleActor, npcActor: NPCBattleActor, winners: List<BattleActor>) = true

    override fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact? = null
}

class DialogueContactProvider(
    val dialogueId: ResourceLocation,
    val lossDialogueId: ResourceLocation?
) : PostBattleContactProvider {
    val dialogue: Dialogue? by lazy { Dialogues.dialogues[dialogueId] }
    val lossDialogue: Dialogue? by lazy { lossDialogueId?.let { Dialogues.dialogues[it] } }

    override fun canShare(playerActor: PlayerBattleActor, npcActor: NPCBattleActor, winners: List<BattleActor>) = true

    override fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact? {
        val player = playerActor.entity ?: return null
        if (lossDialogue != null && !winners.contains(playerActor)) {
            DialogueManager.startDialogue(player, npcActor.entity, lossDialogue!!)
        } else {
            dialogue?.let { DialogueManager.startDialogue(player, npcActor.entity, it) }
        }
        return null
    }
}

class RandomDialogueContactProvider(
    val dialogueIds: List<ResourceLocation>,
    val lossDialogueIds: List<ResourceLocation>?
) : PostBattleContactProvider {
    val dialogues: List<Dialogue> by lazy { dialogueIds.mapNotNull { Dialogues.dialogues[it] } }
    val lossDialogues: List<Dialogue>? by lazy { lossDialogueIds?.mapNotNull { Dialogues.dialogues[it] } }

    override fun canShare(playerActor: PlayerBattleActor, npcActor: NPCBattleActor, winners: List<BattleActor>) = true

    override fun provide(
        contact: PokenavContact,
        playerActor: PlayerBattleActor,
        npcActor: NPCBattleActor,
        winners: List<BattleActor>
    ): PokenavContact? {
        val player = playerActor.entity ?: return null
        if (lossDialogues?.isNotEmpty() == true && !winners.contains(playerActor)) {
            DialogueManager.startDialogue(player, npcActor.entity, lossDialogues!!.random())
        } else if (dialogues.isNotEmpty()) {
            DialogueManager.startDialogue(player, npcActor.entity, dialogues.random())
        }
        return null
    }
}