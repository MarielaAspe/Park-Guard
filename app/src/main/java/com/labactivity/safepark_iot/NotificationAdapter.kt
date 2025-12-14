package com.labactivity.safepark_iot

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// NotificationAdapter.kt
class NotificationAdapter(private val logs: MutableList<NotificationLog>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // Inner class for the ViewHolder
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.location)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        // Note: The "Motion Detected" TextView and ImageView are static in your XML,
        // so we don't need to find them unless their content is dynamic.
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    // Called to bind data to a specific ViewHolder
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val log = logs[position]

        holder.location.text = log.location

        // Format the timestamp for better readability
        holder.timestamp.text = formatTimestamp(log.timestamp)

        // Add an optional click listener
        holder.itemView.setOnClickListener {
            // Handle item click here, e.g., navigate to details
        }
    }

    // Returns the total number of items in the data set
    override fun getItemCount(): Int = logs.size

    /**
     * Helper function to format the timestamp (Long) into a readable string.
     */
    private fun formatTimestamp(timestamp: Timestamp?): String {
        // 1. Handle null case
        timestamp ?: return "Timestamp N/A"

        // Get the time in milliseconds
        val timeInMilli = timestamp.toDate().time
        val now = System.currentTimeMillis()

        // Define the threshold for switching (7 days in milliseconds)
        val SEVEN_DAYS_IN_MILLI = TimeUnit.DAYS.toMillis(7)

        // Check if the timestamp is recent (within the last 7 days)
        if (now - timeInMilli < SEVEN_DAYS_IN_MILLI) {
            // Use Android's built-in utility for relative time formatting
            // This handles "just now", "X minutes ago", "X hours ago", and "yesterday".
            return DateUtils.getRelativeTimeSpanString(
                timeInMilli, // The time to format
                now,          // The time to compare against (current time)
                DateUtils.MINUTE_IN_MILLIS, // The resolution (the smallest unit to display)
                DateUtils.FORMAT_ABBREV_RELATIVE // Optional flag for shorter output
            ).toString()
        } else {
            // If it's older than 7 days, use a standard date and time format

            // Define the desired format for older dates (e.g., "Nov 25, 2025 at 10:30 AM")
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            val date = Date(timeInMilli)

            return dateFormat.format(date)
        }
    }

    /**
     * Updates the adapter's data and refreshes the RecyclerView.
     */
    fun updateData(newLogs: List<NotificationLog>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }
}