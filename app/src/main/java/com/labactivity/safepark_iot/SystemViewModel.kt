package com.labactivity.safepark_iot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.labactivity.safepark_iot.SystemStatus

class SystemViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val _systemStatus = MutableLiveData<SystemStatus>()
    val systemStatus: LiveData<SystemStatus> = _systemStatus
    init {
        listenToSystemStatus()
    }

    fun sendCommand(commandKey: String) {
        dbRef.child("command").child(commandKey).setValue(true)
    }

    private fun listenToSystemStatus() {
        dbRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(SystemStatus::class.java)
                status?.let {
                    _systemStatus.value = it
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Firebase Status Read Failed: ${error.message}")
            }
        })
    }
}