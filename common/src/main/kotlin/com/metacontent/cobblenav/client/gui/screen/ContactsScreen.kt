package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.StatusBarWidget
import com.metacontent.cobblenav.client.gui.widget.button.IconButton
import com.metacontent.cobblenav.client.gui.widget.contacts.ContactWidget
import com.metacontent.cobblenav.client.gui.widget.layout.TableView
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableItemWidget
import com.metacontent.cobblenav.client.gui.widget.layout.scrollable.ScrollableView
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import net.minecraft.network.chat.Component
import java.awt.Color

class ContactsScreen(
    makeOpeningSound: Boolean = false,
    animateOpening: Boolean = false
) : PokenavScreen(makeOpeningSound, animateOpening, Component.literal("Contacts")) {
    override val color = Color.decode("#C3BEA6").rgb

    private lateinit var scrollableView: ScrollableView
    private lateinit var tableView: TableView<ScrollableItemWidget<ContactWidget>>

    override fun initScreen() {
        RadialPopupMenu(
            this,
            screenX + (WIDTH - RadialMenuState.MENU_DIAMETER) / 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - RadialMenuState.MENU_DIAMETER / 2
        ).also { addUnblockableWidget(it) }

        StatusBarWidget(
            screenX + WIDTH - VERTICAL_BORDER_DEPTH - StatusBarWidget.WIDTH - 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - StatusBarWidget.HEIGHT
        ).also { addUnblockableWidget(it) }

        IconButton(
            pX = screenX + VERTICAL_BORDER_DEPTH,
            pY = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - BACK_BUTTON_SIZE,
            pWidth = BACK_BUTTON_SIZE,
            pHeight = BACK_BUTTON_SIZE,
            texture = BACK_BUTTON,
            action = { changeScreen(MainScreen()) }
        ).let { addBlockableWidget(it) }

        tableView = TableView(
            x = screenX + WIDTH - ContactWidget.WIDTH - VERTICAL_BORDER_DEPTH - 1,
            y = screenY + HORIZONTAL_BORDER_DEPTH + 1,
            width = ContactWidget.WIDTH,
            columns = 1,
            horizontalPadding = 0,
            rowHeight = ContactWidget.HEIGHT
        )

        scrollableView = ScrollableView(
            x = tableView.x,
            y = tableView.y,
            width = tableView.width,
            height = HEIGHT - 2 * HORIZONTAL_BORDER_DEPTH - 2,
            child = tableView
        ).also { addBlockableWidget(it) }

        for (i in 0 until 20) {
            tableView.add(ScrollableItemWidget(
                child = ContactWidget(0, 0),
                topEdge = screenY + HORIZONTAL_BORDER_DEPTH + 1,
                bottomEdge = screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - 1
            ))
        }
    }
}