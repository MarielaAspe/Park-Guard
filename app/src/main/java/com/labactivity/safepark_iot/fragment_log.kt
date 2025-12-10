package com.labactivity.safepark_iot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class LogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    private val logList = mutableListOf<LogEntry>()

    private val database = FirebaseDatabase.getInstance("https://safepark-iot-security-system-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewLog)
        adapter = LogAdapter(logList)
        recyclerView.adapter = adapter

        fetchLogEntriesFromFirebase()

        return view
    }

    private fun fetchLogEntriesFromFirebase() {
        database.child("events").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                logList.clear()

                for (dataSnapshot in snapshot.children) {
                    val entry = dataSnapshot.getValue(LogEntry::class.java)
                    if (entry != null) {
                        logList.add(entry)
                    }
                }
                logList.sortByDescending { it.timestamp?.toLongOrNull() }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading log: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}