package com.labactivity.safepark_iot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*

class SnapshotFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SnapshotAdapter
    private val snapshotList = mutableListOf<SnapshotEntry>()

    // IMPORTANT: Get the RTDB reference using your project URL
    private val database = FirebaseDatabase.getInstance("https://safepark-iot-security-system-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Use the updated XML that contains the RecyclerView
        val view = inflater.inflate(R.layout.fragment_snapshot, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewSnapshots)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SnapshotAdapter(snapshotList)
        recyclerView.adapter = adapter

        // Start loading data when the fragment is created
        fetchSnapshotsFromFirebase()

        return view
    }

    private fun fetchSnapshotsFromFirebase() {
        // Listen to changes at the /snapshots/ path
        database.child("snapshots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshotList.clear()

                // Iterate through all the captured snapshots
                for (snapShot in snapshot.children) {
                    // Get the record and map it to our data class
                    val entry = snapShot.getValue(SnapshotEntry::class.java)
                    if (entry != null) {
                        snapshotList.add(entry)
                    }
                }

                // Sort by timestamp (newest first)
                snapshotList.sortByDescending { it.timestamp?.toLongOrNull() }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Error loading snapshots: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}