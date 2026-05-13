package com.metacontent.cobblenav.client.settings

data class CatalogueScreenPreferences(
    val sortingTarget: CatalogueSortingTarget,
    val sorting: Sorting
)

enum class CatalogueSortingTarget { NAME, RESULT_TYPE, BUCKET }