package com.labactivity.safepark_iot

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class NotificationLog(
    @DocumentId
    val id: String = "",
    val location: String = "",
    val timestamp: Timestamp? = null
) {
    constructor() : this("", "", null)}