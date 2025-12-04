package com.metacontent.cobblenav.spawndata

import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.util.readIdentifier
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeIdentifier
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.widget.TextWidget
import com.metacontent.cobblenav.client.gui.widget.section.SectionWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.BlockConditionWidget
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailsWidget
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

data class SpawnData(
    val id: String,
    val result: SpawnResultData,
    val positionType: String,
    val spawnChance: Float,
    val platformId: ResourceLocation?,
    val conditions: List<ConditionData>,
    val anticonditions: List<ConditionData>,
    val blockConditions: BlockConditions,
    val blockAnticonditions: BlockConditions
) : Encodable {
    companion object {
        fun decode(buffer: RegistryFriendlyByteBuf): SpawnData = SpawnData(
            id = buffer.readString(),
            result = SpawnResultData.decode(buffer),
            positionType = buffer.readString(),
            spawnChance = buffer.readFloat(),
            platformId = buffer.readNullable { it.readIdentifier() },
            conditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            anticonditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            blockConditions = BlockConditions.decode(buffer),
            blockAnticonditions = BlockConditions.decode(buffer)
        )
    }

    var chanceMultiplier = 1f

    val dataWidgets: List<AbstractWidget> by lazy {
        val widgets = mutableListOf<AbstractWidget>(
            SectionWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailsWidget.SECTION_WIDTH,
                title = Component.translatable("gui.cobblenav.spawn_data.title.conditions"),
                texts = conditions.map {
                    TextWidget(
                        x = 0,
                        y = 0,
                        width = SpawnDataDetailsWidget.SECTION_WIDTH - 8,
                        text = it.toLine()
                    )
                } + BlockConditionWidget(blockConditions, 0, 0, SpawnDataDetailsWidget.SECTION_WIDTH - 8, 0, 0)
            ),
            SectionWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailsWidget.SECTION_WIDTH,
                title = Component.translatable("gui.cobblenav.spawn_data.title.anticonditions"),
                texts = anticonditions.map {
                    TextWidget(
                        x = 0,
                        y = 0,
                        width = SpawnDataDetailsWidget.SECTION_WIDTH - 8,
                        text = it.toLine()
                    )
                } + BlockConditionWidget(blockAnticonditions, 0, 0, SpawnDataDetailsWidget.SECTION_WIDTH - 8, 0, 0),
                color = RGB(248, 208, 213)
            )
        )

        widgets
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(id)
        result.encode(buffer)
        buffer.writeString(positionType)
        buffer.writeFloat(spawnChance)
        buffer.writeNullable(platformId) { buf, id -> buf.writeIdentifier(id) }
        buffer.writeCollection(conditions) { buf, data -> ConditionData.BUFF_CODEC.encode(buf as RegistryFriendlyByteBuf, data) }
        buffer.writeCollection(anticonditions) { buf, data -> ConditionData.BUFF_CODEC.encode(buf as RegistryFriendlyByteBuf, data) }
        blockConditions.encode(buffer)
        blockAnticonditions.encode(buffer)
    }
}
