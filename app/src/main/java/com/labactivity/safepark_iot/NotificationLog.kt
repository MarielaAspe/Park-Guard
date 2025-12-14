package com.labactivity.safepark_iot

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// NotificationLog.kt
data class NotificationLog(
    // Assuming the document ID from Firestore can serve as a unique ID
    @DocumentId
    val id: String = "",
    // The "location" field from your database
    val location: String = "",
    // The "timestamp" field from your database, using a Long for milliseconds
    val timestamp: Timestamp? = null    // Optional: Add a type/title field if you plan to fetch it (e.g., "Motion Detected")
) {
    // A no-argument constructor is necessary for Firestore's automatic object mapping
    constructor() : this("", "", null)}