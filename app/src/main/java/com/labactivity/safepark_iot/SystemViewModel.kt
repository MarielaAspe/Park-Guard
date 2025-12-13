// In SystemViewModel.kt

package com.labactivity.safepark_iot

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.labactivity.safepark_iot.SystemStatus
import android.util.Log

class SystemViewModel : ViewModel() {

    private val TAG = "SystemViewModel"

    // 1. Setup Firebase
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(
        "https://safepark-iot-security-system-default-rtdb.asia-southeast1.firebasedatabase.app/"
    )
    private val dbReference: DatabaseReference = database.reference

    // 2. LiveData for System Status
    private val _systemStatus = MutableLiveData<SystemStatus>()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    private val statusListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Read the entire database snapshot as the SystemStatus model
            val status = snapshot.getValue(SystemStatus::class.java)
            status?.let { nonNullStatus ->
                _systemStatus.value = nonNullStatus
            } ?: run {
                Log.w(TAG, "Failed to read system status from Firebase.")
                _systemStatus.value = SystemStatus(isArmed = false) // Example default
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, "Failed to read value.", error.toException())
        }
    }

    init {
        // Start listening to the database when the ViewModel is created
        dbReference.addValueEventListener(statusListener)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop listening when the ViewModel is no longer in use
        dbReference.removeEventListener(statusListener)
    }

    // 3. Command Sending Function (to update fields in Firebase)
    fun sendCommand(command: String) {
        val updates = HashMap<String, Any>()

        // Get the current state from LiveData to ensure you only update the field you intend to
        val currentStatus = _systemStatus.value ?: SystemStatus()

        when (command) {
            "arm" -> updates["isArmed"] = true
            "disarm" -> updates["isArmed"] = false
            "activateLight" -> updates["street_light"] = true
            "deactivateLight" -> updates["street_light"] = false

            "raiseGate" -> updates["gateIsOpen"] = true
            "lowerGate" -> updates["gateIsOpen"] = false
            "toggleBuzzer" -> updates["buzzerIsOn"] = !currentStatus.buzzerIsOn

            else -> {
                Log.w(TAG, "Unknown command: $command")
                return
            }
        }

        // Use updateChildren to only modify the specified fields, leaving others untouched
        dbReference.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Command '$command' sent successfully.")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to send command '$command'.", it)
            }
    }
}