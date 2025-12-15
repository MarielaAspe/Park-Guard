package com.labactivity.safepark_iot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.labactivity.safepark_iot.SystemStatus // Required

class DashboardFragment : Fragment() {

    private val viewModel: SystemViewModel by activityViewModels()
    private var isUpdatingUI = false

    // UI References for Switch (Controls command/arm)
    private var switchAlarm: Switch? = null

    // ... (UI References for Status Cards remain the same) ...
    private var gateStatusLayout: View? = null
    private var gateStatusLabel: TextView? = null
    private var gateMainStatusText: TextView? = null
    private var gateStatusIcon: ImageView? = null
    private var gateIndicatorBar: View? = null

    private var modeStatusLayout: View? = null
    private var modeStatusLabel: TextView? = null
    private var modeMainStatusText: TextView? = null
    private var modeStatusIcon: ImageView? = null
    private var modeIndicatorBar: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // --- Find UI Elements ---
        switchAlarm = view.findViewById(R.id.switchAlarm)

        // Button assignments match the new logic:
        val btnLightsOn = view.findViewById<LinearLayout>(R.id.btnActivate)      // Streetlight 1
        val btnLightsOff = view.findViewById<LinearLayout>(R.id.btnDeactivate)    // Streetlight 2
        val btnAutomatic = view.findViewById<LinearLayout>(R.id.btnAutomatic)    // Streetlight 0 (NEW)

        val btnRaiseGate = view.findViewById<LinearLayout>(R.id.btnRaiseGate)
        val btnLowerGate = view.findViewById<LinearLayout>(R.id.btnLowerGate)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

        // ... (Status Card Initialization remains the same) ...
        gateStatusLayout = view.findViewById(R.id.statusArmedCard)
        gateStatusLabel = gateStatusLayout?.findViewById(R.id.statusIndicatorLabel)
        gateMainStatusText = gateStatusLayout?.findViewById(R.id.main_status_text)
        gateStatusIcon = gateStatusLayout?.findViewById(R.id.status_icon)
        gateIndicatorBar = gateStatusLayout?.findViewById(R.id.status_indicator_bar)

        modeStatusLayout = view.findViewById(R.id.statusMotionCard)
        modeStatusLabel = modeStatusLayout?.findViewById(R.id.statusIndicatorLabel)
        modeMainStatusText = modeStatusLayout?.findViewById(R.id.main_status_text)
        modeStatusIcon = modeStatusLayout?.findViewById(R.id.status_icon)
        modeIndicatorBar = modeStatusLayout?.findViewById(R.id.status_indicator_bar)


        // --- LiveData Observer (FIXED for new structure and null safety) ---
        viewModel.systemStatus.observe(viewLifecycleOwner) { status: SystemStatus? ->

            status?.let { currentStatus ->
                // Check if the necessary nested objects are present
                val commandDetails = currentStatus.command
                val statusDetails = currentStatus.status

                if (commandDetails == null || statusDetails == null) {
                    Log.e("SAFEPARK_SYNC", "Nested command or status details are null.")
                    return@let
                }

                Log.d("SAFEPARK_SYNC", "Observer Fired. Status: $currentStatus")

                // 1. Update Arming Switch UI based on /command/arm
                if (switchAlarm?.isChecked != commandDetails.arm) {
                    isUpdatingUI = true
                    switchAlarm?.isChecked = commandDetails.arm
                    isUpdatingUI = false
                }

                // 2. Update Gate Status Card based on /status/gateOpen
                updateGateStatusCard(statusDetails.gateOpen)

                // 3. Update Streetlight Mode Card based on /command/streetlight (0, 1, or 2)
                updateModeStatusCard(commandDetails.streetlight)
            }
        }

        // --- Firebase Write Operations (Updated Paths and Logic) ---

        // 1. Arm/Disarm Switch: Controls command/arm
        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingUI) return@setOnCheckedChangeListener
            // Key is just "arm" since the ViewModel prepends "command/"
            viewModel.updateFirebaseValue("arm", isChecked)

            val statusText = if (isChecked) "ARMED" else "DISARMED"
            Toast.makeText(requireContext(), "System is: $statusText", Toast.LENGTH_SHORT).show()
        }

        // 2. Streetlight Controls: Controls command/streetlight (1, 2, 0)
        btnLightsOn?.setOnClickListener {
            viewModel.updateFirebaseValue("streetlight", 1) // 1 = ON
            Toast.makeText(requireContext(), "Street Light: Manual ON", Toast.LENGTH_SHORT).show()
        }

        btnLightsOff?.setOnClickListener {
            viewModel.updateFirebaseValue("streetlight", 2) // 2 = OFF
            Toast.makeText(requireContext(), "Street Light: Manual OFF", Toast.LENGTH_SHORT).show()
        }

        btnAutomatic?.setOnClickListener {
            viewModel.updateFirebaseValue("streetlight", 0) // 0 = Automatic
            Toast.makeText(requireContext(), "Street Light: Automatic Mode", Toast.LENGTH_SHORT).show()
        }

        // 3. Gate Controls: Controls command/openGate and command/closeGate
        btnRaiseGate?.setOnClickListener {
            viewModel.updateFirebaseValue("openGate", true)
            viewModel.updateFirebaseValue("closeGate", false)
            Toast.makeText(requireContext(), "Gate OPEN Request Sent", Toast.LENGTH_SHORT).show()
        }

        btnLowerGate?.setOnClickListener {
            viewModel.updateFirebaseValue("closeGate", true)
            viewModel.updateFirebaseValue("openGate", false)
            Toast.makeText(requireContext(), "Gate CLOSE Request Sent", Toast.LENGTH_SHORT).show()
        }

        // 4. Logout Button
        btnLogout?.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun logoutUser() {
        try {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        } catch (e: Exception) {
            Log.e("LOGOUT", "Logout failed: ${e.message}")
            Toast.makeText(requireContext(), "Logout failed. Check Firebase setup.", Toast.LENGTH_SHORT).show()
        }

    }

    // --- Status Card 1: Gate Status (Gate Position) ---
    private fun updateGateStatusCard(isOpen: Boolean) {
        val context = context ?: return

        val mainText: String
        val iconRes: Int
        val colorRes: Int
        val backgroundRes: Int

        if (isOpen) {
            mainText = "OPEN"
            iconRes = R.drawable.ic_warning_toast
            colorRes = R.color.warning_orange
            backgroundRes = R.drawable.bg_orange_rounded
        } else {
            mainText = "CLOSED"
            iconRes = R.drawable.ic_check
            colorRes = R.color.success_green
            backgroundRes = R.drawable.bg_green_rounded
        }

        gateStatusLabel?.text = "GATE POSITION"
        gateMainStatusText?.text = mainText
        gateMainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        gateStatusIcon?.setImageResource(iconRes)
        gateStatusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))
        gateIndicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))
        gateStatusIcon?.background = ContextCompat.getDrawable(context, backgroundRes)
    }

    // --- Status Card 2: System Mode (Streetlight Status) ---
    private fun updateModeStatusCard(currentStreetlightMode: Int) {
        val context = context ?: return

        val labelText: String = "STREETLIGHT MODE"
        val mainText: String
        val iconRes: Int
        val colorRes: Int
        val backgroundRes: Int

        when (currentStreetlightMode) {
            0 -> { // Automatic Mode
                mainText = "AUTOMATIC: AUTO-LIGHTING"
                iconRes = R.drawable.ic_mode_day // Assuming you have an auto mode icon
                colorRes = R.color.default_blue // Use a distinct color for Auto
                backgroundRes = R.drawable.bg_blue_rounded
            }
            1 -> { // Manual ON Mode
                mainText = "MANUAL: LIGHTS ON"
                iconRes = R.drawable.ic_mode_night // Icon for Lights ON
                colorRes = R.color.warning_orange // Use a brighter color for ON
                backgroundRes = R.drawable.bg_orange_rounded
            }
            2 -> { // Manual OFF Mode
                mainText = "MANUAL: LIGHTS OFF"
                iconRes = R.drawable.ic_mode_day // Icon for Lights OFF
                colorRes = R.color.day_mode // Use a lighter color for OFF
                backgroundRes = R.drawable.bg_yellow_rounded
            }
            else -> { // Fallback/Error State
                mainText = "UNKNOWN MODE"
                iconRes = R.drawable.ic_warning_toast
                colorRes = R.color.alert_red
                backgroundRes = R.drawable.bg_red_rounded
            }
        }

        modeStatusLabel?.text = labelText
        modeMainStatusText?.text = mainText
        modeMainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        modeStatusIcon?.setImageResource(iconRes)
        modeStatusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))
        modeIndicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))
        modeStatusIcon?.background = ContextCompat.getDrawable(context, backgroundRes)
    }
}