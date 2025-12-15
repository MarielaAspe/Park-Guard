package com.labactivity.safepark_iot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var headerTitle: TextView
    private lateinit var btnLogout: ImageButton

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "FirebaseApp initialized successfully in MainActivity.")
        } catch (e: Exception) {
            Log.e("Firebase", "Failed to initialize FirebaseApp. Check google-services.json.", e)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        headerTitle = findViewById(R.id.header_title)

        btnLogout = findViewById(R.id.btn_logout)

        if (savedInstanceState == null) {

            replaceFragment(DashboardFragment(), "ParkGuard")
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }

    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
        headerTitle.text = title
    }

    private fun logoutUser() {
        try {
            firebaseAuth.signOut()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("LOGOUT", "Logout failed: ${e.message}")
            Toast.makeText(this, "Logout failed. Check Firebase setup.", Toast.LENGTH_SHORT).show()
        }
    }
}