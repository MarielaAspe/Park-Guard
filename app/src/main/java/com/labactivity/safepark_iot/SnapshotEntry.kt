package com.labactivity.safepark_iot

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SnapshotEntry(
    val timestamp: String? = null,
    val type: String? = null,
    val imageUrl: String? = null
)