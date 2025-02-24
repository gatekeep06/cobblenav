package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.mixin.GrowingPlantBlockMixin
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.GrowingPlantBlock

data class BlockConditions(
    val blocks: MutableSet<ResourceLocation>
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = BlockConditions(
            buffer.readList { it.readResourceLocation() }.toMutableSet()
        )
    }

    val asItemStacks by lazy {
        blocks.map {
            var block = BuiltInRegistries.BLOCK.get(it)
            if (block is GrowingPlantBlock) {
                block = (block as GrowingPlantBlockMixin).invokeGetHeadBlock()
            }
            ItemStack(block)
        }
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(blocks) { buf, block -> buf.writeResourceLocation(block) }
    }
}
