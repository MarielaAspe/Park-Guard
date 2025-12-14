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
import kotlin.collections.getValue
class SystemViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://safepark-iot-security-system-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    private val _systemStatus = MutableLiveData<SystemStatus>()
    val systemStatus: LiveData<SystemStatus> = _systemStatus

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(SystemStatus::class.java)
                _systemStatus.value = status ?: SystemStatus()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_ERROR", "Failed to read value.", error.toException())
            }
        })
    }

    fun updateFirebaseValue(key: String, value: Boolean) {
        database.child(key).setValue(value)
            .addOnFailureListener {
                Log.e("FIREBASE_WRITE", "Failed to write $key.", it)
            }
    }

}