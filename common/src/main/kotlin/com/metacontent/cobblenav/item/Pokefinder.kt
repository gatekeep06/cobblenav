package com.metacontent.cobblenav.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Pokefinder : Item(Properties().stacksTo(1)) {
    companion object {
        const val BASE_REGISTRY_KEY: String = "pokefinder_item_"
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        return super.use(level, player, interactionHand)
    }
}