package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.mixin.GrowingPlantBlockMixin
import com.metacontent.cobblenav.util.getHeadBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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
            if (it.path == "water") return@map ItemStack(Items.WATER_BUCKET)

            val block = BuiltInRegistries.BLOCK.get(it).let { block ->
                if (block is GrowingPlantBlock) {
                    block.getHeadBlock()
                } else {
                    block
                }
            }
            ItemStack(block)
        }
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(blocks) { buf, block -> buf.writeResourceLocation(block) }
    }
}
