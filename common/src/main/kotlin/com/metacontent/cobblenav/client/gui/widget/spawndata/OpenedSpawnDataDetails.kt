package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.metacontent.cobblenav.client.gui.util.pushAndPop
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.stateful.WidgetState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.Component
import org.joml.Vector3d
import org.joml.Vector3f

class OpenedSpawnDataDetails(
    statefulWidget: SpawnDataDetailsWidget, x: Int, y: Int
) : WidgetState<SpawnDataDetailsWidget>(
    statefulWidget,
    x,
    y,
    SpawnDataDetailsWidget.WIDTH,
    SpawnDataDetailsWidget.HEIGHT,
    Component.literal("Opened Spawn Data Details")
) {
    override val blockScreenWidgets = true

    init {
        addWidget(statefulWidget.closeButton)
    }

    private val tableView = TableView<AbstractWidget>(
        x = statefulWidget.sectionX,
        y = statefulWidget.sectionY,
        width = SpawnDataDetailsWidget.SECTION_WIDTH,
        columns = 1,
        verticalGap = 2f,
        horizontalGap = 0f
    ).also { statefulWidget.displayer.selectedData?.let { data -> it.add(data.conditionWidgets) } }
    private val scrollableView = ScrollableView(
        x = statefulWidget.menuX,
        y = statefulWidget.sectionY,
        width = SpawnDataDetailsWidget.MENU_WIDTH,
        height = SpawnDataDetailsWidget.SCROLLABLE_HEIGHT,
        scissorSpreading = 10,
        child = tableView
    ).also { addWidget(it) }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (!statefulWidget.displayer.isDataSelected()) {
            removeWidget(statefulWidget.closeButton)
            removeWidget(scrollableView)
            statefulWidget.changeState(ClosedSpawnDataDetails(statefulWidget, x, y))
        }

        val poseStack = guiGraphics.pose()

        statefulWidget.renderMenu(guiGraphics, i, j, f)

        val scale = 5f
        poseStack.pushAndPop(
            scale = Vector3f(scale, scale, scale)
        ) {
            statefulWidget.displayer.selectedData?.result?.drawResult(
                poseStack = poseStack,
                x = (x + ((statefulWidget.width - SpawnDataDetailsWidget.MENU_WIDTH) / 2)) / scale,
                y = (y + 20) / scale,
                z = 0f / scale,
                delta = f / 10
            )
        }

        poseStack.pushAndPop(
            translate = Vector3d(0.0, 0.0, 3000.0)
        ) {
            scrollableView.render(guiGraphics, i, j, f)
        }
    }
}