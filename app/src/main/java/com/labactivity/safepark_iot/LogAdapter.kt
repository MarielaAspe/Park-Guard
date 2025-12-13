package com.labactivity.safepark_iot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Note: We rely on LogEntry to handle all formatting, so no extra date imports are needed here.

/**
 * Adapter for the log list, now compatible with LogEntry(location, timestamp).
 */
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

        // --- 1. Set the Location Text (Using only the 'location' field) ---
        // Since 'status' is removed, the 'location' field holds the main event description/zone.
        holder.location.text = currentItem.location ?: "Unknown Event/Zone"


        // --- 2. Format and Set Date and Time using LogEntry Helpers ---
        // Relies on getFormattedDate() and getFormattedTime() added in the LogEntry class above.
        holder.date.text = currentItem.getFormattedDate()
        holder.time.text = currentItem.getFormattedTime()


        // --- 3. Set up Video Icon ---
        // Since we have no 'status' to filter by, we default to showing the icon
        // or you can set a default behavior based on your app's requirements.
        holder.videoIcon.visibility = View.VISIBLE

        holder.videoIcon.setOnClickListener {
            // TODO: Implement navigation or action to view the related video
        }
    }

    override fun getItemCount() = logList.size

    fun updateData(newLogList: List<LogEntry>) {
        logList.clear()
        logList.addAll(newLogList)
        notifyDataSetChanged()
    }
}