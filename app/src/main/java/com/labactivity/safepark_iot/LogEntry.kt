package com.labactivity.safepark_iot

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class LogEntry(
    val location: String? = null,

    val timestamp: Timestamp? = null
) {

    fun getFormattedDate(): String {
        return timestamp?.let {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return dateFormat.format(it.toDate())
        } ?: "N/A"
    }

    fun getFormattedTime(): String {
        return timestamp?.let {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            return timeFormat.format(it.toDate())
        } ?: "N/A"
    }

    fun getFormattedTimestamp(): String {
        return timestamp?.let {
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
            return sdf.format(it.toDate())
        } ?: "N/A"
    }
}