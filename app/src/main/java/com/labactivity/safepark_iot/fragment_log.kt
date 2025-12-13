package com.labactivity.safepark_iot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query // Import Query for orderBy


class LogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    private val logList = mutableListOf<LogEntry>()

    // 1. Initialize Firebase Firestore instance
    private val firestoreDb = FirebaseFirestore.getInstance()

    // Define your Firestore collection and field names
    private val COLLECTION_NAME = "log"
    private val TIMESTAMP_FIELD = "timestamp" // Use this for sorting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewLog)
        adapter = LogAdapter(logList)
        recyclerView.adapter = adapter

        fetchLogEntriesFromFirestore()

        return view
    }

    private fun fetchLogEntriesFromFirestore() {
        // 2. Build the Firestore query - Retrieving ALL documents in the 'log' collection
        firestoreDb.collection(COLLECTION_NAME)
            // Sort by 'timestamp' in descending order (most recent first)
            .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error -> // Use snapshotListener for real-time updates
                if (error != null) {
                    Log.w("LogFragment", "Listen failed.", error)
                    Toast.makeText(
                        requireContext(),
                        "Error loading log: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addSnapshotListener
                }

                logList.clear()

                if (snapshot != null) {
                    // Loop through all documents and convert them to LogEntry objects
                    for (document in snapshot.documents) {
                        val entry = document.toObject(LogEntry::class.java)
                        if (entry != null) {
                            logList.add(entry)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d("LogFragment", "Current data: null")
                }
            }
    }
}