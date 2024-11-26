package com.metacontent.cobblenav.util;

import net.minecraft.nbt.CompoundTag;

public interface PreferencesSaver {
    String SAVED_PREFERENCES_KEY = "cobblenav_saved_preferences";
    String BUCKET_INDEX_KEY = "bucket_index";
    String SORTING_KEY = "sorting_key";
    String APPLY_BUCKET_KEY = "apply_bucket";

    CompoundTag cobblenav$getSavedPreferences();
}
