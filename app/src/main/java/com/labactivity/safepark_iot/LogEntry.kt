package com.labactivity.safepark_iot

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LogEntry(
    val timestamp: String? = null,

    val type: String? = null,

    val videoUrl: String? = null

    // val size: Int? = null
)