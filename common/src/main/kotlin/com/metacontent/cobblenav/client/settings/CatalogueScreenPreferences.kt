package com.metacontent.cobblenav.client.settings

import com.metacontent.cobblenav.client.gui.util.translate
import net.minecraft.network.chat.MutableComponent

data class CatalogueScreenPreferences(
    val sortingTarget: CatalogueSortingTarget,
    val sorting: Sorting
)

enum class CatalogueSortingTarget(val displayName: MutableComponent) {
    NAME(translate("gui.cobblenav.sorting.name")),
    RESULT_TYPE(translate("gui.cobblenav.sorting.result_type")),
    BUCKET(translate("gui.cobblenav.sorting.bucket"))
}