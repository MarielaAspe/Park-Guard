package com.labactivity.safepark_iot

import android.os.Bundle
import android.util.Log
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

class DashboardFragment : Fragment() {

    private val viewModel: SystemViewModel by activityViewModels()
    private var isUpdatingUI = false
    private var switchAlarm: Switch? = null
    // private var switchArming: Switch? = null
    private var armingStatusLayout: View? = null
    private var armingStatusLabel: TextView? = null
    private var armingMainStatusText: TextView? = null
    private var armingStatusIcon: ImageView? = null
    private var armingIndicatorBar: View? = null
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

        val btnLightsOn = view.findViewById<LinearLayout>(R.id.btnActivate)
        val btnLightsOff = view.findViewById<LinearLayout>(R.id.btnDeactivate)
        val btnAutomatic = view.findViewById<LinearLayout>(R.id.btnAutomatic)
        val btnRaiseGate = view.findViewById<LinearLayout>(R.id.btnRaiseGate)
        val btnLowerGate = view.findViewById<LinearLayout>(R.id.btnLowerGate)

        armingStatusLayout = view.findViewById(R.id.statusArmingCard)
        armingStatusLabel = armingStatusLayout?.findViewById(R.id.statusIndicatorLabel)
        armingMainStatusText = armingStatusLayout?.findViewById(R.id.main_status_text)
        armingStatusIcon = armingStatusLayout?.findViewById(R.id.status_icon)
        armingIndicatorBar = armingStatusLayout?.findViewById(R.id.status_indicator_bar)

        gateStatusLayout = view.findViewById(R.id.statusGateCard)
        gateStatusLabel = gateStatusLayout?.findViewById(R.id.statusIndicatorLabel)
        gateMainStatusText = gateStatusLayout?.findViewById(R.id.main_status_text)
        gateStatusIcon = gateStatusLayout?.findViewById(R.id.status_icon)
        gateIndicatorBar = gateStatusLayout?.findViewById(R.id.status_indicator_bar)

        modeStatusLayout = view.findViewById(R.id.statusModeCard)
        modeStatusLabel = modeStatusLayout?.findViewById(R.id.statusIndicatorLabel)
        modeMainStatusText = modeStatusLayout?.findViewById(R.id.main_status_text)
        modeStatusIcon = modeStatusLayout?.findViewById(R.id.status_icon)
        modeIndicatorBar = modeStatusLayout?.findViewById(R.id.status_indicator_bar)


        viewModel.systemStatus.observe(viewLifecycleOwner) { status: SystemStatus? ->

            status?.let { currentStatus ->
                val commandDetails = currentStatus.command
                val statusDetails = currentStatus.status

                if (commandDetails == null || statusDetails == null) {
                    Log.e("SAFEPARK_SYNC", "Nested command or status details are null.")
                    return@let
                }

                Log.d("SAFEPARK_SYNC", "Observer Fired. Status: $currentStatus")

                if (switchAlarm?.isChecked != commandDetails.alarm) {
                    isUpdatingUI = true
                    switchAlarm?.isChecked = commandDetails.alarm
                    isUpdatingUI = false
                }

                updateArmingStatusCard(statusDetails.ir)
                updateGateStatusCard(statusDetails.gateOpen)
                updateModeStatusCard(commandDetails.streetlight)
            }
        }


        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingUI) return@setOnCheckedChangeListener
            viewModel.updateFirebaseValue("alarm", isChecked)
            val statusText = if (isChecked) "Alarm ON" else "Alarm OFF"
            Toast.makeText(requireContext(), "Motion Alarm is: $statusText", Toast.LENGTH_SHORT).show()
        }

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

        return view
    }


    private fun updateArmingStatusCard(irDetected: Boolean) {
        val context = context ?: return

        val mainText: String
        val iconRes: Int
        val colorRes: Int
        val backgroundRes: Int
        val toastMessage: String
        if (irDetected) {
            mainText = "IR DETECTED"
            iconRes = R.drawable.ic_warning_toast
            colorRes = R.color.alert_red
            backgroundRes = R.drawable.bg_red_rounded
            toastMessage = "Alert! Intrusion detected by IR sensor."
        } else {
            mainText = "SYSTEM NORMAL"
            iconRes = R.drawable.ic_check
            colorRes = R.color.success_green
            backgroundRes = R.drawable.bg_green_rounded
            toastMessage = "System status is now Normal."
        }

        armingStatusLabel?.text = "IR SENSOR STATUS" // Changed Label
        armingMainStatusText?.text = mainText
        armingMainStatusText?.setTextColor(ContextCompat.getColor(context, colorRes))
        armingStatusIcon?.setImageResource(iconRes)
        armingStatusIcon?.setColorFilter(ContextCompat.getColor(context, android.R.color.white))
        armingIndicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))
        armingStatusIcon?.background = ContextCompat.getDrawable(context, backgroundRes)

        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

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

    private fun updateModeStatusCard(currentStreetlightMode: Int) {
        val context = context ?: return

        val labelText: String = "STREETLIGHT MODE"
        val mainText: String
        val iconRes: Int
        val colorRes: Int
        val backgroundRes: Int

        when (currentStreetlightMode) {
            0 -> {
                mainText = "AUTOMATIC: AUTO-LIGHTING"
                iconRes = R.drawable.ic_automatic
                colorRes = R.color.default_blue
                backgroundRes = R.drawable.bg_blue_rounded
            }
            1 -> {
                mainText = "MANUAL: LIGHTS ON"
                iconRes = R.drawable.ic_mode_day
                colorRes = R.color.warning_orange
                backgroundRes = R.drawable.bg_orange_rounded
            }
            2 -> {
                mainText = "MANUAL: LIGHTS OFF"
                iconRes = R.drawable.ic_mode_night
                colorRes = R.color.default_blue
                backgroundRes = R.drawable.bg_yellow_rounded
            }
            else -> {
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