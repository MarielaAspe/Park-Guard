package com.labactivity.safepark_iot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val logList: MutableList<LogEntry>) :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val location: TextView = itemView.findViewById(R.id.location)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val icon: ImageView = itemView.findViewById(R.id.imageViewIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val currentItem = logList[position]

        holder.location.text = currentItem.location ?: "Unknown Event/Zone"


        val timeAndDate = "${currentItem.getFormattedTime()} - ${currentItem.getFormattedDate()}"
        holder.timestamp.text = timeAndDate


        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount() = logList.size

    fun updateData(newLogList: List<LogEntry>) {
        logList.clear()
        logList.addAll(newLogList)
        notifyDataSetChanged()
    }

    fun clearData() {
        logList.clear()
        notifyDataSetChanged()
    }
}