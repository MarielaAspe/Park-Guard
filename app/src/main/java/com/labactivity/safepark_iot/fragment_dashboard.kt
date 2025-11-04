package com.labactivity.safepark_iot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.content.ContextCompat
import com.labactivity.safepark_iot.R
import com.labactivity.safepark_iot.viewmodel.SystemViewModel // Assuming SystemViewModel path

class DashboardFragment : Fragment() {

    // Get the shared ViewModel instance
    private val viewModel: SystemViewModel by activityViewModels()

    // Flag to prevent observer from triggering command sends on initial load
    private var isUpdatingUI = false

    // --- References for the new single status CardView components (IDs from the updated XML) ---
    private var mainStatusText: TextView? = null // ID: main_status_text
    private var statusIcon: ImageView? = null     // ID: status_icon
    private var indicatorBar: View? = null        // ID: status_indicator_bar
    private var statusLabel: TextView? = null     // ID: statusIndicatorLabel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // --- UI References ---
        val switchAlarm = view.findViewById<Switch>(R.id.switchAlarm)
        val btnActivate = view.findViewById<LinearLayout>(R.id.btnActivate)
        val btnDeactivate = view.findViewById<LinearLayout>(R.id.btnDeactivate)
        val btnRaiseGate = view.findViewById<LinearLayout>(R.id.btnRaiseGate)
        val btnLowerGate = view.findViewById<LinearLayout>(R.id.btnLowerGate)

        // 1. Initialize New Status Card References
        // NOTE: Ensure the status CardView is included in R.layout.fragment_dashboard using <include> or is a part of the layout.
        mainStatusText = view.findViewById(R.id.main_status_text)
        statusIcon = view.findViewById(R.id.status_icon)
        indicatorBar = view.findViewById(R.id.status_indicator_bar)
        statusLabel = view.findViewById(R.id.statusIndicatorLabel)


        // 2. Set up Observers to update UI when Firebase data changes
        viewModel.systemStatus.observe(viewLifecycleOwner) { status ->
            isUpdatingUI = true

            // Update the main Alarm Switch state
            switchAlarm?.isChecked = status.armed

            // Update the single status CardView based on priority
            updateCombinedStatusCard(
                isArmed = status.armed,
                isMotion = status.motion,
                isGateOpen = status.gate, // Assuming 'true' means OPEN/Violation
                isOnline = status.online
            )

            isUpdatingUI = false
        }

        // --- 3. Switch Listener: Sends ARM/DISARM command to Firebase ---
        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingUI) return@setOnCheckedChangeListener

            if (isChecked) {
                viewModel.sendCommand("arm")
                Toast.makeText(requireContext(), "Sending ARM Command...", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendCommand("disarm")
                Toast.makeText(requireContext(), "Sending DISARM Command...", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 4. Button Listeners: Use ViewModel functions ---
        btnActivate?.setOnClickListener {
            viewModel.sendCommand("arm")
            Toast.makeText(requireContext(), "Sending ARM Command...", Toast.LENGTH_SHORT).show()
        }

        btnDeactivate?.setOnClickListener {
            viewModel.sendCommand("disarm")
            Toast.makeText(requireContext(), "Sending DISARM Command...", Toast.LENGTH_SHORT).show()
        }

        btnRaiseGate?.setOnClickListener {
            viewModel.sendCommand("raiseGate")
            Toast.makeText(requireContext(), "Sending RAISE GATE Command...", Toast.LENGTH_SHORT).show()
        }

        btnLowerGate?.setOnClickListener {
            viewModel.sendCommand("lowerGate")
            Toast.makeText(requireContext(), "Sending LOWER GATE Command...", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    /**
     * Updates the combined status CardView based on system priority.
     * Priority: MOTION ALERT > ARMED > GATE OPEN > OFFLINE > DISARMED (SAFE)
     */
    private fun updateCombinedStatusCard(
        isArmed: Boolean,
        isMotion: Boolean,
        isGateOpen: Boolean,
        isOnline: Boolean
    ) {
        val context = requireContext()
        val text: String
        val iconRes: Int
        val colorRes: Int
        val bgDrawableRes: Int

        if (isMotion) {
            text = "MOTION ALERT"
            iconRes = R.drawable.ic_warning_toast
            colorRes = R.color.alert_red
            bgDrawableRes = R.drawable.bg_red_rounded
        }
        else if (isArmed) {
            text = "ARMED"
            iconRes = R.drawable.ic_armed_status
            colorRes = R.color.success_green
            bgDrawableRes = R.drawable.bg_green_rounded
        }
        else if (isGateOpen) {
            text = "GATE OPEN"
            iconRes = R.drawable.ic_arrow_up
            colorRes = R.color.warning_orange
            bgDrawableRes = R.drawable.bg_orange_rounded
        }
        else if (!isOnline) {
            text = "OFFLINE"
            iconRes = R.drawable.ic_offline_status
            colorRes = android.R.color.darker_gray
            bgDrawableRes = R.drawable.bg_gray_rounded
        }
        else {
            text = "DISARMED"
            iconRes = R.drawable.ic_disarmed_status
            colorRes = R.color.default_blue
            bgDrawableRes = R.drawable.bg_blue_rounded
        }

        mainStatusText?.text = text
        mainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        statusIcon?.setImageResource(iconRes)
        statusIcon?.background = ContextCompat.getDrawable(context, bgDrawableRes)
        statusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))

        indicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))

        statusLabel?.text = if (isOnline && !isMotion) "SECURITY STATUS" else "SYSTEM STATUS"
    }
}