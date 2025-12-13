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



    // Flag to prevent the observer from triggering the listener loops

    private var isUpdatingUI = false



    private var mainStatusText: TextView? = null

    private var statusIcon: ImageView? = null

    private var indicatorBar: View? = null

    private var statusLabel: TextView? = null

    private var switchAlarm: Switch? = null // Made class property



    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)



        // --- UI References ---

        switchAlarm = view.findViewById(R.id.switchAlarm) // Assigned first

        // ... other UI element assignments (btnActivate, etc.) ...

        val btnActivate = view.findViewById<LinearLayout>(R.id.btnActivate)

        val btnDeactivate = view.findViewById<LinearLayout>(R.id.btnDeactivate)

        val btnRaiseGate = view.findViewById<LinearLayout>(R.id.btnRaiseGate)

        val btnLowerGate = view.findViewById<LinearLayout>(R.id.btnLowerGate)



        mainStatusText = view.findViewById(R.id.main_status_text)

        statusIcon = view.findViewById(R.id.status_icon)

        indicatorBar = view.findViewById(R.id.status_indicator_bar)

        statusLabel = view.findViewById(R.id.statusIndicatorLabel)



        // --- Observer (Source of Truth) ---

        viewModel.systemStatus.observe(viewLifecycleOwner) { status ->

            if (status != null) {

                Log.d("SAFEPARK_SYNC", "Observer Fired. DB isArmed: ${status.isArmed}")



                // 1. Switch Synchronization

                // Check if the current switch state differs from the database state.

                if (switchAlarm?.isChecked != status.isArmed) {

                    isUpdatingUI = true // Pause listener

                    switchAlarm?.isChecked = status.isArmed

                    isUpdatingUI = false // Resume listener

                    Log.d("SAFEPARK_SYNC", "Switch synced to ${status.isArmed}")

                }



                // 2. Status Card Synchronization

                // Update the card based on the *confirmed* status from the database.

                updateCombinedStatusCard(status)

            }

        }



        // --- Listeners (User Action + Pessimistic Update) ---

        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->

            // Block the listener if the change was programmatic (from the observer)

            if (isUpdatingUI) return@setOnCheckedChangeListener



            val command: String

            val statusText: String



            if (isChecked) {

                command = "arm"

                statusText = "ARMED"

            } else {

                command = "disarm"

                statusText = "DISARMED"

            }



            // 1. Send command to Firebase

            viewModel.sendCommand(command)

            Toast.makeText(requireContext(), "System command sent: $statusText", Toast.LENGTH_SHORT).show()



            // 2. IMMEDIATE VISUAL FEEDBACK (Pessimistic Update)

            // Immediately update the status card to match the user's action for responsiveness.

            val temporaryStatus = SystemStatus(isArmed = isChecked)

            updateCombinedStatusCard(temporaryStatus)

        }



        // --- Other Listeners ---

        btnActivate?.setOnClickListener { viewModel.sendCommand("activateLight") }

        btnDeactivate?.setOnClickListener { viewModel.sendCommand("deactivateLight") }

        btnRaiseGate?.setOnClickListener { viewModel.sendCommand("raiseGate") }

        btnLowerGate?.setOnClickListener { viewModel.sendCommand("lowerGate") }



        return view

    }



    // --- Status Update Logic ---

    private fun updateCombinedStatusCard(status: SystemStatus) {

        val context = context ?: return



        val text: String

        val iconRes: Int

        val colorRes: Int

        val bgDrawableRes: Int



        Log.d("SAFEPARK_SYNC", "updateCombinedStatusCard called. Input isArmed: ${status.isArmed}")



        if (status.isArmed) {

            text = "ARMED"

            iconRes = R.drawable.ic_armed_status

            colorRes = R.color.success_green

            bgDrawableRes = R.drawable.bg_green_rounded

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

        statusLabel?.text = "SECURITY STATUS"

        indicatorBar?.setBackgroundColor(ContextCompat.getColor(context, colorRes))

    }

}