package com.labactivity.safepark_iot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.labactivity.safepark_iot.databinding.FragmentSnapshotBinding

class SnapshotFragment : Fragment() {

    private lateinit var binding: FragmentSnapshotBinding
    // RENAME: Renamed adapter type to LogAdapter to match the provided adapter class
    private lateinit var logAdapter: LogAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val logCollection = firestore.collection("log")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSnapshotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchNotifications()

        binding.clearAll.setOnClickListener {
            clearAllNotifications()
        }
    }

    /**
     * Sets up the RecyclerView with the adapter and layout manager.
     */
    private fun setupRecyclerView() {
        // FIX: Ensure initialization uses LogAdapter and expects LogEntry list
        logAdapter = LogAdapter(mutableListOf())

        binding.recyclerViewSnapshots.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Fetches notification logs from the Firestore database.
     */
    private fun fetchNotifications() {
        logCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // FIX: Map documents to the data class the adapter expects (LogEntry)
                val logs = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(LogEntry::class.java)
                }

                // FIX: Pass the 'logs' list to the updateData function
                logAdapter.updateData(logs)
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationFragment", "Error fetching documents: $exception")
                Toast.makeText(context, "Error fetching notifications.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Deletes all documents in the "log" collection using Firestore batch operations.
     */
    private fun clearAllNotifications() {
        logCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(context, "Log is already clear.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Create a WriteBatch instance for efficient mass deletion
                val batch = firestore.batch()

                // Add all documents in the QuerySnapshot to the batch for deletion
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                // Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        // Success: Clear the RecyclerView and show a message
                        logAdapter.clearData()
                        Toast.makeText(context, "All notification logs cleared.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Failure: Handle error
                        Log.e("NotificationFragment", "Batch delete failed: $e")
                        Toast.makeText(context, "Failed to clear logs: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("NotificationFragment", "Error querying documents for deletion: $e")
                Toast.makeText(context, "Error reading database for clearing.", Toast.LENGTH_SHORT).show()
            }
    }
}