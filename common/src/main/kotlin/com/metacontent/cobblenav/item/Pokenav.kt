package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.screen.MainScreen
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class Pokenav(private val model: PokenavModelType) : Item(Properties().stacksTo(MAX_STACK)) {
    companion object {
        const val MAX_STACK = 1
        const val BASE_REGISTRY_KEY = "pokenav_item_"
        const val TRANSLATION_KEY = "item.cobblenav.pokenav_item"
        const val BASE_TOOLTIP_TRANSLATION_KEY = "item.cobblenav.pokenav_item."
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide()) {
            if (CobblenavClient.trackArrowOverlay.tracking) {
                CobblenavClient.trackArrowOverlay.tracking = false
            }
            else {
                Minecraft.getInstance().setScreen(MainScreen(makeOpeningSound = true, animateOpening = true))
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(interactionHand))
    }

    override fun getDescriptionId(itemStack: ItemStack): String {
        return TRANSLATION_KEY
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        list.add(Component.translatable(BASE_TOOLTIP_TRANSLATION_KEY + model.modelName).withStyle(ChatFormatting.GRAY))
    }
}