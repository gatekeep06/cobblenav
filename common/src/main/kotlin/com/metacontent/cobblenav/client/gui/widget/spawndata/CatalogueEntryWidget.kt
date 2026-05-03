package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.metacontent.cobblenav.client.gui.util.cobblenavScissor
import com.metacontent.cobblenav.client.gui.util.gui
import com.metacontent.cobblenav.client.gui.util.literal
import com.metacontent.cobblenav.client.gui.util.translate
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.spawndata.resultdata.displayName
import net.minecraft.client.gui.GuiGraphics

class CatalogueEntryWidget(
    val spawnData: SpawnData
) : SoundlessWidget(0, 0, WIDTH, HEIGHT, spawnData.result.getResultName()) {
    companion object {
        const val WIDTH = 87
        const val HEIGHT = 59
        const val PORTRAIT_X = 4
        const val PORTRAIT_Y = 15
        const val PORTRAIT_WIDTH = 20
        const val PORTRAIT_HEIGHT = 20
        const val ID_WIDTH = 40
        const val INFO_X = 37
        const val INFO_Y = 31
        const val INFO_WIDTH = 13

        val BACKGROUND = gui("catalogue/entry_bg")
        val ENTRY = gui("catalogue/entry")
    }

    private val knowledge = spawnData.result.getResultKnowledge()

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()

        blitk(
            matrixStack = poseStack,
            texture = BACKGROUND,
            x = x,
            y = y,
            width = width,
            height = height
        )

        guiGraphics.cobblenavScissor(
            x1 = x + PORTRAIT_X,
            y1 = y + PORTRAIT_Y,
            x2 = x + PORTRAIT_X + PORTRAIT_WIDTH,
            y2 = y + PORTRAIT_Y + PORTRAIT_HEIGHT
        )

        spawnData.result.drawPortrait(
            poseStack = poseStack,
            x = (x + PORTRAIT_X + 8).toFloat(),
            y = (y + PORTRAIT_Y - 12).toFloat(),
            z = 500f
        )

        guiGraphics.disableScissor()

        poseStack.translate(0f, 0f, 1000f)

        blitk(
            matrixStack = poseStack,
            texture = ENTRY,
            x = x,
            y = y,
            width = width,
            height = height
        )

        drawScaledText(
            context = guiGraphics,
            text = literal(spawnData.id),
            x = x + 4,
            y = y + 4,
            scale = 0.5f,
            maxCharacterWidth = (ID_WIDTH / 0.5f).toInt()
        )
        drawScaledText(
            context = guiGraphics,
            text = translate("bucket.cobblenav.${spawnData.bucket}", literal(spawnData.bucket).red()),
            x = x + INFO_X - 0.5,
            y = y + INFO_Y,
            scale = 0.4f,
//            maxCharacterWidth = ((INFO_WIDTH + 4) / 0.5f).toInt(),
            centered = true
        )
        drawScaledText(
            context = guiGraphics,
            text = literal("${spawnData.weight}"),
            x = x + INFO_X + INFO_WIDTH + 4.5,
            y = y + INFO_Y,
            scale = 0.4f,
//            maxCharacterWidth = ((INFO_WIDTH + 4) / 0.5f).toInt(),
            centered = true
        )
        drawScaledText(
            context = guiGraphics,
            text = translate(knowledge.translateKey),
            x = x + INFO_X + INFO_WIDTH * 2 + 10,
            y = y + INFO_Y,
            scale = 0.4f,
//            maxCharacterWidth = ((INFO_WIDTH + 4) / 0.5f).toInt(),
            centered = true
        )

        drawScaledText(
            context = guiGraphics,
            text = spawnData.result.displayName().append(" | ").append(spawnData.getPositionTypeName()),
            x = x + PORTRAIT_X,
            y = y + PORTRAIT_Y + PORTRAIT_HEIGHT + 14,
            scale = 0.4f
        )
        drawScaledText(
            context = guiGraphics,
            text = spawnData.result.getResultName(),
            x = x + PORTRAIT_X,
            y = y + PORTRAIT_Y + PORTRAIT_HEIGHT + 6,
            scale = 0.75f
        )

        poseStack.popPose()
    }
}