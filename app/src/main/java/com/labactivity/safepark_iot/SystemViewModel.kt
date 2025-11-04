package com.labactivity.safepark_iot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.labactivity.safepark_iot.SystemStatus // Assuming your model is here

class SystemViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference

    // LiveData to hold the system's current status
    private val _systemStatus = MutableLiveData<SystemStatus>()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    init {
        // Start listening to the Firebase status node immediately
        listenToSystemStatus()
    }

    // Function used by the DashboardFragment to send commands
    fun sendCommand(commandKey: String) {
        // The path MUST match the checkFirebaseCommands() logic in your ESP32
        dbRef.child("command").child(commandKey).setValue(true)
    }

    private fun listenToSystemStatus() {
        // The path MUST match the updateFirebaseStatus() logic in your ESP32
        dbRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Deserialize the data into your Kotlin data class
                val status = snapshot.getValue(SystemStatus::class.java)
                status?.let {
                    _systemStatus.value = it
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error or handle it gracefully
                println("Firebase Status Read Failed: ${error.message}")
            }
        })
    }
}