package com.labactivity.safepark_iot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter(private val logList: MutableList<LogEntry>) :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.textViewLocation)
        val date: TextView = itemView.findViewById(R.id.textViewDate)
        val time: TextView = itemView.findViewById(R.id.textViewTime)
        val videoIcon: ImageView = itemView.findViewById(R.id.imageViewVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val currentItem = logList[position]

        // --- 1. Format Timestamp to Date and Time ---
        currentItem.timestamp?.toLongOrNull()?.let { ms ->
            // Date format: 10-2-2025
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            // Time format: 10:30PM
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            holder.date.text = dateFormat.format(Date(ms))
            holder.time.text = timeFormat.format(Date(ms))
        }

        holder.location.text = when (currentItem.type?.lowercase()) {
            "motion" -> "Motion Alert" // Based on ESP32 code's log
            "arm" -> "System Armed"
            "disarm" -> "System Disarmed"
            else -> "System Event"
        }


        holder.videoIcon.visibility = View.VISIBLE

        holder.videoIcon.setOnClickListener {

        }
    }

    override fun getItemCount() = logList.size

    fun updateData(newLogList: List<LogEntry>) {
        logList.clear()
        logList.addAll(newLogList)
        notifyDataSetChanged()
    }
}