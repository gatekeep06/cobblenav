package com.metacontent.cobblenav.item

import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.item.interactive.PokerodItem
import com.metacontent.cobblenav.networking.packet.client.OpenFishingnavPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.isTraveling
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class Fishingnav : Item(Properties().stacksTo(1)) {
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (player.handSlots.any { it.`is`(CobblemonItemTags.POKE_RODS) } && !player.isShiftKeyDown) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand))
        }
        if (!level.isClientSide()) {
            val bobber = player.fishing
            if (bobber is PokeRodFishingBobberEntity && bobber.isTraveling()) {
                return InteractionResultHolder.fail(player.getItemInHand(interactionHand))
            }
            (player as? ServerPlayer)?.let {
                OpenFishingnavPacket(PokenavOS("Fishing", canUseFishingAid = true)).sendToPlayer(it)
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), false)
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        list.add(Component.translatable("item.cobblenav.fishingnav_item.tooltip").gray())
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}