package com.metacontent.cobblenav.client.settings

import com.metacontent.cobblenav.client.settings.Sorting

data class LocationScreenPreferences(
    val bucketIndex: Int,
    val sorting: Sorting,
    val applyBucketChecked: Boolean
)
