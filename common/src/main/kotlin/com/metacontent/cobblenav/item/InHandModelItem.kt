package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.isGui
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

interface InHandModelItem {
    val inventoryModel: ResourceLocation
    val inHandModel: ResourceLocation

    fun getModel(stack: ItemStack, displayContext: ItemDisplayContext): ResourceLocation {
        return if (displayContext.isGui()) {
            inventoryModel
        } else {
            inHandModel
        }
    }
}