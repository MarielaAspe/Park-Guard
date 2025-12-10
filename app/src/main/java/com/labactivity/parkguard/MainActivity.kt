package com.labactivity.parkguard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var headerTitle: TextView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headerTitle = findViewById(R.id.header_title)
        bottomNav = findViewById(R.id.bottom_nav)

        // ✅ Show label text only for the active (selected) item
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED

        // ✅ Load default fragment on startup
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment(), "Dashboard")
            bottomNav.selectedItemId = R.id.dashboardFragment
        }

        // ✅ Handle bottom navigation item clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> {
                    replaceFragment(DashboardFragment(), "Dashboard")
                    true
                }
                R.id.logsFragment -> {
                    replaceFragment(LogFragment(), "System Logs")
                    true
                }
                R.id.snapshotFragment -> {
                    replaceFragment(SnapshotFragment(), "Intrusion Snapshot")
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
