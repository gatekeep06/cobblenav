package com.metacontent.cobblenav.client.settings

import com.metacontent.cobblenav.client.gui.util.Sorting

class PokenavSettings : Settings<PokenavSettings>() {
    companion object {
        const val NAME = "pokenav"
    }

    @Transient
    override val name = NAME

    var preferences = PokenavPreferences(
        bucketIndex = 0,
        sorting = Sorting.ASCENDING,
        applyBucketChecked = true
    )
        set(value) {
            changed = true
            field = value
        }

    var shareContacts = true
        set(value) {
            changed = true
            field = value
        }
}