package com.labactivity.safepark_iot

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SnapshotAdapter(private val snapshotList: MutableList<SnapshotEntry>) :
    RecyclerView.Adapter<SnapshotAdapter.SnapshotViewHolder>() {

    class SnapshotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
        val alertType: TextView = itemView.findViewById(R.id.textViewAlertType)
        val snapshotImage: ImageView = itemView.findViewById(R.id.imageViewSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_snapshot, parent, false)
        return SnapshotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnapshotViewHolder, position: Int) {
        val currentItem = snapshotList[position]
        val context = holder.itemView.context

        holder.alertType.text = currentItem.type ?: "Unknown Event"

        currentItem.timestamp?.toLongOrNull()?.let { ms ->
            val formatter = SimpleDateFormat("h:mm a, MMM dd, yyyy", Locale.getDefault())
            holder.timestamp.text = formatter.format(Date(ms))
        }

        val colorInt = when (currentItem.type?.lowercase()) {
            "motion intrusion" -> Color.parseColor("#FF4500")
            "cleared" -> Color.parseColor("#2ECC71")
            else -> Color.parseColor("#666666")
        }
        holder.alertType.setTextColor(colorInt)

        currentItem.imageUrl?.let { url ->
            Glide.with(context)
                .load(url)

                .into(holder.snapshotImage)
        }
    }

    override fun getItemCount() = snapshotList.size
}