package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Pokefinder(
    color: String
) : Item(Properties().stacksTo(1)), InHandModelItem, FlickeringItem, OpenableItem {
    companion object {
        const val BASE_REGISTRY_KEY: String = "pokefinder_item_"
        const val TRANSLATION_KEY = "item.cobblenav.pokefinder_item"
    }

    override val inventoryModel = cobblenavResource("$BASE_REGISTRY_KEY$color")
    override val flickeringInventoryModel = cobblenavResource("flicker/$BASE_REGISTRY_KEY$color")
    override val openedInventoryModel = cobblenavResource("open/$BASE_REGISTRY_KEY$color")
    override val inHandModel = cobblenavResource("model/$BASE_REGISTRY_KEY$color")
    override val flickeringInHandModel = cobblenavResource("model/flicker/$BASE_REGISTRY_KEY$color")
    override val openedInHandModel = cobblenavResource("model/open/$BASE_REGISTRY_KEY$color")

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(PokefinderScreen())
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), false)
    }

    override fun getDescriptionId(): String {
        return TRANSLATION_KEY
    }

    override fun isOpened(stack: ItemStack) = Minecraft.getInstance().screen is PokefinderScreen
}