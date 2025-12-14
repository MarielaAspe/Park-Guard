package com.labactivity.safepark_iot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.labactivity.safepark_iot.databinding.FragmentSnapshotBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SnapshotFragment : Fragment() {

    private lateinit var binding: FragmentSnapshotBinding // Using View Binding
    private lateinit var logAdapter: NotificationAdapter
    private val firestore = FirebaseFirestore.getInstance()

    // Assuming your fragment layout is named 'fragment_notification.xml'
    // but using the provided constraint layout name for reference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Use View Binding if available, otherwise use findViewById
        // For this example, I'll assume standard View Binding for better practice.
        // Replace with your actual binding class name if different
        binding = FragmentSnapshotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchNotifications()

        binding.clearAll.setOnClickListener {
            // Handle Clear All logic here, e.g., deleting all logs from Firestore
            Toast.makeText(context, "Clear All clicked (Not yet implemented)", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sets up the RecyclerView with the adapter and layout manager.
     */
    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list
        logAdapter = NotificationAdapter(mutableListOf())

        // The layout manager is already set in your XML, but we can set it here too
        binding.recyclerViewSnapshots.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Fetches notification logs from the Firestore database.
     */
    private fun fetchNotifications() {
        firestore.collection("log") // "log collection" from your description
            // Order by timestamp to show the newest first
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Convert the list of documents to a list of NotificationLog objects
                val notifications = querySnapshot.documents.mapNotNull { document ->
                    // 'toObject' requires the no-arg constructor in the data class
                    document.toObject(NotificationLog::class.java)
                }

                // Update the RecyclerView adapter with the new data
                logAdapter.updateData(notifications)
            }
            .addOnFailureListener { exception ->
                // Handle the error (e.g., display a Toast or a retry button)
                Log.e("NotificationFragment", "Error fetching documents: $exception")
                Toast.makeText(context, "Error fetching notifications.", Toast.LENGTH_SHORT).show()
            }
    }
}