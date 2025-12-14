package com.labactivity.safepark_iot

import android.content.Intent // Added for navigation
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
import com.labactivity.safepark_iot.SystemStatus

class DashboardFragment : Fragment() {

    private val viewModel: SystemViewModel by activityViewModels()
    private var isUpdatingUI = false

    // UI References for Switch
    private var switchAlarm: Switch? = null

    // UI References for Status Card 1 (Gate Status)
    private var gateStatusLayout: View? = null
    private var gateStatusLabel: TextView? = null
    private var gateMainStatusText: TextView? = null
    private var gateStatusIcon: ImageView? = null
    private var gateIndicatorBar: View? = null

    // UI References for Status Card 2 (Mode Status)
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

        val btnActivate = view.findViewById<LinearLayout>(R.id.btnActivate)
        val btnDeactivate = view.findViewById<LinearLayout>(R.id.btnDeactivate)
        val btnRaiseGate = view.findViewById<LinearLayout>(R.id.btnRaiseGate)
        val btnLowerGate = view.findViewById<LinearLayout>(R.id.btnLowerGate)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

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


        viewModel.systemStatus.observe(viewLifecycleOwner) { status ->
            if (status != null) {
                Log.d("SAFEPARK_SYNC", "Observer Fired. Status: $status")

                if (switchAlarm?.isChecked != status.buzzerIsOn) {
                    isUpdatingUI = true
                    switchAlarm?.isChecked = status.buzzerIsOn
                    isUpdatingUI = false
                }

                updateGateStatusCard(status.gateIsOpen)
                updateModeStatusCard(status.streetLight)
            }
        }

        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingUI) return@setOnCheckedChangeListener

            viewModel.updateFirebaseValue("buzzerIsOn", isChecked)

            val statusText = if (isChecked) "ON" else "OFF"
            Toast.makeText(requireContext(), "Buzzer set to: $statusText", Toast.LENGTH_SHORT).show()
        }

        btnActivate?.setOnClickListener {
            viewModel.updateFirebaseValue("street_light", true)
            Toast.makeText(requireContext(), "Street Light ON", Toast.LENGTH_SHORT).show()
        }
        btnDeactivate?.setOnClickListener {
            viewModel.updateFirebaseValue("street_light", false)
            Toast.makeText(requireContext(), "Street Light OFF", Toast.LENGTH_SHORT).show()
        }

        btnRaiseGate?.setOnClickListener {
            viewModel.updateFirebaseValue("gateIsOpen", true)
            Toast.makeText(requireContext(), "Opening Gate...", Toast.LENGTH_SHORT).show()
        }
        btnLowerGate?.setOnClickListener {
            viewModel.updateFirebaseValue("gateIsOpen", false)
            Toast.makeText(requireContext(), "Closing Gate...", Toast.LENGTH_SHORT).show()
        }

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

            // Finish the current activity that hosts this fragment
            activity?.finish()

        } catch (e: Exception) {
            Log.e("LOGOUT", "Logout failed: ${e.message}")
            Toast.makeText(requireContext(), "Logout failed. Check Firebase setup.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Status Card 1: Gate Status (Gate Position) ---
    private fun updateGateStatusCard(isOpen: Boolean) {
        val context = context ?: return

        val labelText: String
        val mainText: String
        val iconRes: Int
        val colorRes: Int

        if (isOpen) {
            labelText = "GATE POSITION"
            mainText = "OPEN"
            iconRes = R.drawable.ic_warning_toast
            colorRes = R.color.warning_orange
        } else {
            labelText = "GATE POSITION"
            mainText = "CLOSED"
            iconRes = R.drawable.ic_check
            colorRes = R.color.success_green
        }

        gateStatusLabel?.text = labelText
        gateMainStatusText?.text = mainText
        gateMainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        gateStatusIcon?.setImageResource(iconRes)
        gateStatusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))
        gateIndicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))
        gateStatusIcon?.background = ContextCompat.getDrawable(
            context,
            if (isOpen) R.drawable.bg_orange_rounded else R.drawable.bg_green_rounded
        )
    }

    private fun updateModeStatusCard(lightsOn: Boolean) {
        val context = context ?: return

        val labelText: String
        val mainText: String
        val iconRes: Int
        val colorRes: Int

        if (lightsOn) {
            labelText = "SYSTEM MODE"
            mainText = "NIGHT MODE: LIGHTS ON"
            iconRes = R.drawable.ic_mode_night
            colorRes = R.color.default_blue
        } else {
            labelText = "SYSTEM MODE"
            mainText = "DAY MODE: LIGHTS OFF"
            iconRes = R.drawable.ic_mode_day
            colorRes = R.color.day_mode
        }

        modeStatusLabel?.text = labelText
        modeMainStatusText?.text = mainText
        modeMainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        modeStatusIcon?.setImageResource(iconRes)
        modeStatusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))
        modeIndicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))

        modeStatusIcon?.background = ContextCompat.getDrawable(
            context,
            if (lightsOn) R.drawable.bg_blue_rounded else R.drawable.bg_yellow_rounded
        )
    }
}