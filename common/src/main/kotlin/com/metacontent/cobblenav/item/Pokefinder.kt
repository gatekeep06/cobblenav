package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Pokefinder : Item(Properties().stacksTo(1)) {
    companion object {
        const val BASE_REGISTRY_KEY: String = "pokefinder_item_"
        const val TRANSLATION_KEY = "item.cobblenav.pokefinder_item"
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(PokefinderScreen())
            return InteractionResultHolder.success(player.getItemInHand(interactionHand))
        }
        return InteractionResultHolder.fail(player.getItemInHand(interactionHand))
    }

    override fun getDescriptionId(): String {
        return TRANSLATION_KEY
    }
}