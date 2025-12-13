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
import com.metacontent.cobblenav.client.gui.widget.spawndata.SpawnDataDetailWidget
import com.metacontent.cobblenav.event.CobblenavEvents
import com.metacontent.cobblenav.event.SpawnDataWidgetsCreatedEvent
import com.metacontent.cobblenav.spawndata.resultdata.SpawnResultData
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

data class SpawnData(
    val id: String,
    val result: SpawnResultData,
    val positionType: String,
    val bucket: String,
    val weight: Float,
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
            bucket = buffer.readString(),
            weight = buffer.readFloat(),
            platformId = buffer.readNullable { it.readIdentifier() },
            conditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            anticonditions = buffer.readList { ConditionData.BUFF_CODEC.decode(it as RegistryFriendlyByteBuf) },
            blockConditions = BlockConditions.decode(buffer),
            blockAnticonditions = BlockConditions.decode(buffer)
        )
    }

    val dataWidgets: List<AbstractWidget> by lazy {
        val conditionWidgets: MutableList<AbstractWidget> = conditions.map {
            TextWidget(x = 0, y = 0, width = SpawnDataDetailWidget.SECTION_WIDTH - 8, text = it.toLine())
        }.toMutableList()
        blockConditions.takeIf { it.isNotEmpty() }?.let {
            conditionWidgets.add(
                BlockConditionWidget(
                    blockConditions = it,
                    x = 0,
                    y = 0,
                    width = SpawnDataDetailWidget.SECTION_WIDTH - 5,
                    horizontalGap = 0,
                    verticalGap = 0
                )
            )
        }
        CobblenavEvents.CONDITION_SECTION_WIDGETS_CREATED.emit(
            SpawnDataWidgetsCreatedEvent(this, conditionWidgets)
        )

        val anticonditionWidgets: MutableList<AbstractWidget> = anticonditions.map {
            TextWidget(x = 0, y = 0, width = SpawnDataDetailWidget.SECTION_WIDTH - 8, text = it.toLine())
        }.toMutableList()
        blockAnticonditions.takeIf { it.isNotEmpty() }?.let {
            anticonditionWidgets.add(
                BlockConditionWidget(
                    blockConditions = it,
                    x = 0,
                    y = 0,
                    width = SpawnDataDetailWidget.SECTION_WIDTH - 5,
                    horizontalGap = 0,
                    verticalGap = 0
                )
            )
        }
        CobblenavEvents.ANTICONDITION_SECTION_WIDGETS_CREATED.emit(
            SpawnDataWidgetsCreatedEvent(this, anticonditionWidgets)
        )

        val widgets = mutableListOf<AbstractWidget>()

        result.dataWidgets?.let { widgets.addAll(it) }

        widgets.add(
            SectionWidget(
                x = 0,
                y = 0,
                width = SpawnDataDetailWidget.SECTION_WIDTH,
                title = Component.translatable("gui.cobblenav.spawn_data.title.rarity"),
                widgets = listOf(
                    TextWidget(
                        x = 0,
                        y = 0,
                        width = SpawnDataDetailWidget.SECTION_WIDTH - 8,
                        text = Component.translatable("gui.cobblenav.spawn_data.bucket")
                            .append(Component.translatable("bucket.cobblenav.${bucket}"))
                    ),
                    TextWidget(
                        x = 0,
                        y = 0,
                        width = SpawnDataDetailWidget.SECTION_WIDTH - 8,
                        text = Component.translatable("gui.cobblenav.spawn_data.weight", weight)
                    )
                ),
                color = RGB(214, 180, 252)
            )
        )

        conditionWidgets.takeIf { it.isNotEmpty() }?.let {
            widgets.add(
                SectionWidget(
                    x = 0,
                    y = 0,
                    width = SpawnDataDetailWidget.SECTION_WIDTH,
                    title = Component.translatable("gui.cobblenav.spawn_data.title.conditions"),
                    widgets = it
                )
            )
        }
        anticonditionWidgets.takeIf { it.isNotEmpty() }?.let {
            widgets.add(
                SectionWidget(
                    x = 0,
                    y = 0,
                    width = SpawnDataDetailWidget.SECTION_WIDTH,
                    title = Component.translatable("gui.cobblenav.spawn_data.title.anticonditions"),
                    widgets = it,
                    color = RGB(248, 208, 213)
                )
            )
        }

        CobblenavEvents.SPAWN_DATA_WIDGETS_CREATED.emit(
            SpawnDataWidgetsCreatedEvent(this, widgets)
        )

        widgets
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeString(id)
        result.encode(buffer)
        buffer.writeString(positionType)
        buffer.writeString(bucket)
        buffer.writeFloat(weight)
        buffer.writeNullable(platformId) { buf, id -> buf.writeIdentifier(id) }
        buffer.writeCollection(conditions) { buf, data ->
            ConditionData.BUFF_CODEC.encode(
                buf as RegistryFriendlyByteBuf, data
            )
        }
        buffer.writeCollection(anticonditions) { buf, data ->
            ConditionData.BUFF_CODEC.encode(
                buf as RegistryFriendlyByteBuf, data
            )
        }
        blockConditions.encode(buffer)
        blockAnticonditions.encode(buffer)
    }
}