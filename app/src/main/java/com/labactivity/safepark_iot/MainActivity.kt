package com.labactivity.safepark_iot

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var headerTitle: TextView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "FirebaseApp initialized successfully in MainActivity.")
        } catch (e: Exception) {
            Log.e("Firebase", "Failed to initialize FirebaseApp. Check google-services.json.", e)
        }

        headerTitle = findViewById(R.id.header_title)
        bottomNav = findViewById(R.id.bottom_nav)

        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED

        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment(), "Dashboard")
            bottomNav.selectedItemId = R.id.dashboardFragment
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> {
                    replaceFragment(DashboardFragment(), "Dashboard")
                    true
                }

                R.id.snapshotFragment -> {
                    replaceFragment(SnapshotFragment(), "Notifications")
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
        headerTitle.text = title
    }
}