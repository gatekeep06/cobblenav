package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.client.render.drawScaledTextJustifiedRight
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor
import org.joml.Vector3d
import org.joml.Vector3f

class OpenedSpawnDataDetail(
    statefulWidget: SpawnDataDetailWidget, x: Int, y: Int
) : WidgetState<SpawnDataDetailWidget>(
    statefulWidget,
    x,
    y,
    SpawnDataDetailWidget.WIDTH,
    SpawnDataDetailWidget.HEIGHT,
    Component.literal("Opened Spawn Data Details")
) {
    companion object {
        const val BUTTON_WIDTH: Int = 17
        const val BUTTON_HEIGHT: Int = 23
        val NEXT = gui("button/next_button_big")
        val PREV = gui("button/prev_button_big")
    }

    override val blockScreenWidgets = true

    private val tableView = TableView<AbstractWidget>(
        x = statefulWidget.sectionX,
        y = statefulWidget.sectionY,
        width = SpawnDataDetailWidget.SECTION_WIDTH,
        columns = 1,
        verticalGap = 2f,
        horizontalGap = 0f
    ).also { statefulWidget.displayer.selectedData?.let { data -> it.add(data.dataWidgets) } }
    private val scrollableView = ScrollableView(
        x = statefulWidget.menuX,
        y = statefulWidget.sectionY,
        width = SpawnDataDetailWidget.MENU_WIDTH,
        height = SpawnDataDetailWidget.SCROLLABLE_HEIGHT,
        scissorSpreading = 7,
        child = tableView
    ).also { addWidget(it) }
    private val prevButton = IconButton(
        pX = x + (statefulWidget.width - SpawnDataDetailWidget.MENU_WIDTH) / 2 - 75,
        pY = y + (height - BUTTON_HEIGHT) / 2,
        pWidth = BUTTON_WIDTH,
        pHeight = BUTTON_HEIGHT,
        action = { checkButtons(statefulWidget.displayer.switchData(-1)) },
        texture = PREV
    ).also { addWidget(it) }
    private val nextButton = IconButton(
        pX = x + (statefulWidget.width - SpawnDataDetailWidget.MENU_WIDTH) / 2 + 75 - BUTTON_WIDTH,
        pY = y + (height - BUTTON_HEIGHT) / 2,
        pWidth = BUTTON_WIDTH,
        pHeight = BUTTON_HEIGHT,
        action = { checkButtons(statefulWidget.displayer.switchData(1)) },
        texture = NEXT
    ).also { addWidget(it) }

    init {
        addWidget(statefulWidget.closeButton)
        statefulWidget.displayer.selectedData?.let { data ->
            statefulWidget.displayer.displayedData?.indexOf(data)?.let { index ->
                checkButtons(index)
            }
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (!statefulWidget.displayer.isDataSelected()) {
            removeWidget(statefulWidget.closeButton)
            removeWidget(scrollableView)
            removeWidget(prevButton)
            removeWidget(nextButton)
            statefulWidget.changeState(ClosedSpawnDataDetail(statefulWidget, x, y))
        }

        statefulWidget.displayer.selectedData?.dataWidgets?.let { tableView.items = it.toMutableList() }

        val poseStack = guiGraphics.pose()

        statefulWidget.renderMenu(guiGraphics, i, j, f)

        val scale = 5f
        poseStack.pushAndPop(
            scale = Vector3f(scale, scale, scale)
        ) {
            statefulWidget.displayer.selectedData?.result?.drawResult(
                poseStack = poseStack,
                x = (x + (statefulWidget.width - SpawnDataDetailWidget.MENU_WIDTH) / 2) / scale,
                y = (y + 20) / scale,
                z = 0f / scale,
                delta = f / 10
            )
        }

        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 3000.0)
        ) {
            drawScaledTextJustifiedRight(
                context = guiGraphics,
                text = Component.literal(statefulWidget.displayer.selectedData?.id ?: ""),
                x = statefulWidget.menuX + SpawnDataDetailWidget.MENU_WIDTH - 1,
                y = y + statefulWidget.height - 4 - Minecraft.getInstance().font.lineHeight * 0.5f,
                scaleX = 1f,
                scaleY = 0.5f,
                colour = FastColor.ARGB32.color(40, 99, 125, 138)
            )
            scrollableView.render(guiGraphics, i, j, f)
            prevButton.render(guiGraphics, i, j, f)
            nextButton.render(guiGraphics, i, j, f)
        }
    }

    private fun checkButtons(currentIndex: Int) {
        prevButton.disabled = currentIndex <= 0
        nextButton.disabled = currentIndex >= (statefulWidget.displayer.displayedData?.size ?: 0) - 1
    }
}