package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.networking.packet.client.OpenFishingnavPacket
import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Fishingnav : Item(Properties().stacksTo(1)) {
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide()) {
            (player as? ServerPlayer)?.let {
                OpenFishingnavPacket(PokenavOS("Fishing", canUseFishingAid = true)).sendToPlayer(it)
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), false)
    }
}