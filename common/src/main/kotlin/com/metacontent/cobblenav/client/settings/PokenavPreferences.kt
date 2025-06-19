package com.metacontent.cobblenav.client.settings

import com.metacontent.cobblenav.client.gui.util.Sorting

data class PokenavPreferences(
    val bucketIndex: Int,
    val sorting: Sorting,
    val applyBucketChecked: Boolean
)
