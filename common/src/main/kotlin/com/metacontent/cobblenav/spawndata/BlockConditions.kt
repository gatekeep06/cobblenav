package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.util.getHeadBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.GrowingPlantBlock

data class BlockConditions(
    val blocks: MutableSet<ResourceLocation>
) : Collection<ResourceLocation>, Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf) = BlockConditions(
            buffer.readList { it.readResourceLocation() }.toMutableSet()
        )
    }

    override val size: Int
        get() = blocks.size

    val asItemStacks by lazy {
        blocks.map {
            when (it.path) {
                "water" -> ItemStack(Items.WATER_BUCKET)
                "lava" -> ItemStack(Items.LAVA_BUCKET)
                "frosted_ice" -> ItemStack(Blocks.ICE)
                else -> {
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
        }
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeCollection(blocks) { buf, block -> buf.writeResourceLocation(block) }
    }

    override fun iterator() = blocks.iterator()

    override fun isEmpty() = blocks.isEmpty()

    override fun contains(element: ResourceLocation) = blocks.contains(element)

    override fun containsAll(elements: Collection<ResourceLocation>) = blocks.containsAll(elements)
}
