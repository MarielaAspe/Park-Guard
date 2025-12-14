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

class NotificationAdapter(private val logs: MutableList<NotificationLog>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.location)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val log = logs[position]

        holder.location.text = log.location

        holder.timestamp.text = formatTimestamp(log.timestamp)

        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = logs.size

    private fun formatTimestamp(timestamp: Timestamp?): String {
        timestamp ?: return "Timestamp N/A"

        val timeInMilli = timestamp.toDate().time
        val now = System.currentTimeMillis()

        val SEVEN_DAYS_IN_MILLI = TimeUnit.DAYS.toMillis(7)

        if (now - timeInMilli < SEVEN_DAYS_IN_MILLI) {
            return DateUtils.getRelativeTimeSpanString(
                timeInMilli,
                now,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        } else {

            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            val date = Date(timeInMilli)

            return dateFormat.format(date)
        }
    }

    fun updateData(newLogs: List<NotificationLog>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }
}