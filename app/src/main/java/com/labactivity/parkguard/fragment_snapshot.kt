package com.labactivity.parkguard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SnapshotFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_snapshot, container, false)

        val imageSnap1 = view.findViewById<ImageView>(R.id.imageSnap1)
        val imageSnap2 = view.findViewById<ImageView>(R.id.imageSnap2)

        // Example click behavior
        imageSnap1.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Intrusion Snapshot 1...", Toast.LENGTH_SHORT).show()
        }

        imageSnap2.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Snapshot 2...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
