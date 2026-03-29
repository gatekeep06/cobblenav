package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.isGui
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

interface OpenableItem {
    val openedInventoryModel: ResourceLocation
    val openedInHandModel: ResourceLocation

    fun getOpenedModel(stack: ItemStack, displayContext: ItemDisplayContext): ResourceLocation? {
        return if (displayContext.isGui()) {
            openedInventoryModel
        } else if (displayContext.firstPerson()) {
            openedInHandModel
        } else {
            null
        }
    }

    fun isOpened(stack: ItemStack): Boolean
}