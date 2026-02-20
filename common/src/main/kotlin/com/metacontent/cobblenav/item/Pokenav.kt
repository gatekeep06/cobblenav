package com.metacontent.cobblenav.item

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity
import com.cobblemon.mod.common.util.raycast
import com.metacontent.cobblenav.client.gui.screen.FishingnavScreen
import com.metacontent.cobblenav.client.gui.screen.PokenavScreen
import com.metacontent.cobblenav.networking.packet.client.OpenPokenavPacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level

class Pokenav(
    private val model: PokenavModelType
) : Item(Properties().stacksTo(MAX_STACK)), InHandModelItem, FlickeringItem, OpenableItem {
    companion object {
        const val MAX_STACK = 1
        const val BASE_REGISTRY_KEY = "pokenav_item_"
        const val TRANSLATION_KEY = "item.cobblenav.pokenav_item"
        const val BASE_TOOLTIP_TRANSLATION_KEY = "item.cobblenav.pokenav_item."
    }

    override val inventoryModel = cobblenavResource("$BASE_REGISTRY_KEY${model.modelName}")
    override val flickeringInventoryModel = cobblenavResource("flicker/$BASE_REGISTRY_KEY${model.modelName}")
    override val openedInventoryModel = cobblenavResource("open/$BASE_REGISTRY_KEY${model.modelName}")
    override val inHandModel = cobblenavResource("model/$BASE_REGISTRY_KEY${model.modelName}")
    override val flickeringInHandModel = cobblenavResource("model/flicker/$BASE_REGISTRY_KEY${model.modelName}")
    override val openedInHandModel = cobblenavResource("model/open/$BASE_REGISTRY_KEY${model.modelName}")

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide()) {
            (player as? ServerPlayer)?.let { serverPlayer ->
                val posUsedOn = serverPlayer.raycast(
                    player.blockInteractionRange().toFloat(),
                    ClipContext.Fluid.NONE
                ).blockPos
                val hasAreaSpawner = level.getBlockEntity(posUsedOn)?.let { it is PokeSnackBlockEntity } == true
                OpenPokenavPacket(
                    os = PokenavOS("Lite", canUseLocation = true),
                    fixedAreaPoint = posUsedOn.takeIf { hasAreaSpawner }
                ).sendToPlayer(serverPlayer)
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), false)
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

    override fun isOpened(stack: ItemStack) = Minecraft.getInstance().screen.let {
        it is PokenavScreen && it !is FishingnavScreen
    }
}