package com.labactivity.parkguard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

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

        // --- Switch Listener ---
        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Alarm Activated" else "Alarm Deactivated"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // --- Button Listeners ---
        btnActivate?.setOnClickListener {
            Toast.makeText(requireContext(), "System Activated", Toast.LENGTH_SHORT).show()
        }

        btnDeactivate?.setOnClickListener {
            Toast.makeText(requireContext(), "System Deactivated", Toast.LENGTH_SHORT).show()
        }

        btnRaiseGate?.setOnClickListener {
            Toast.makeText(requireContext(), "Raising Gate...", Toast.LENGTH_SHORT).show()
        }

        btnLowerGate?.setOnClickListener {
            Toast.makeText(requireContext(), "Lowering Gate...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
