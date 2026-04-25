package com.metacontent.cobblenav.util

import com.metacontent.cobblenav.client.gui.util.literal

data class CobblenavResourcePack(
    val id: String,
    val name: String,
    val enabledByDefault: Boolean
) {
    val resourceLocation = cobblenavResource(id)
    val displayName = literal(name)
}
