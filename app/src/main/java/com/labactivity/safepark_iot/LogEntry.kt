package com.labactivity.safepark_iot

// LogEntry.kt
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class to map documents from the Firestore 'log' collection.
 * Documents are assumed to contain only 'location' and 'timestamp'.
 */
data class LogEntry(
    // Field 1: String field for the location (e.g., "North", "Main Gate")
    val location: String? = null,

    // Field 2: Correctly maps the native Firestore Timestamp object
    val timestamp: Timestamp? = null
) {
    // --- Helper Functions for Date/Time Formatting ---

    /**
     * Returns the formatted date part (e.g., "13-12-2025"). Used for textViewDate.
     */
    fun getFormattedDate(): String {
        return timestamp?.let {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return dateFormat.format(it.toDate())
        } ?: "N/A"
    }

    /**
     * Returns the formatted time part (e.g., "10:30 PM"). Used for textViewTime.
     */
    fun getFormattedTime(): String {
        return timestamp?.let {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            return timeFormat.format(it.toDate())
        } ?: "N/A"
    }

    /**
     * Returns a single formatted string combining date and time (e.g., "Dec 13, 2025 10:30:00 PM").
     */
    fun getFormattedTimestamp(): String {
        return timestamp?.let {
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
            return sdf.format(it.toDate())
        } ?: "N/A"
    }
}