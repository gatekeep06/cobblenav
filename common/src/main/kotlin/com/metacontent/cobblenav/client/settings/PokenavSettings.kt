package com.metacontent.cobblenav.client.settings

class PokenavSettings : Settings<PokenavSettings>() {
    companion object {
        const val NAME = "pokenav"
    }

    @Transient
    override val name = NAME

    var locationPreferences = LocationScreenPreferences(
        bucketIndex = 0,
        sorting = Sorting.ASCENDING,
        applyBucketChecked = true
    )
        set(value) {
            changed = true
            field = value
        }

    var cataloguePreferences = CatalogueScreenPreferences(
        sortingTarget = CatalogueSortingTarget.NAME,
        sorting = Sorting.ASCENDING
    )
        set(value) {
            changed = true
            field = value
        }
}