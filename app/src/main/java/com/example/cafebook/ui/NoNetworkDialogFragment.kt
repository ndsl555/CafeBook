package com.example.cafebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.cafebook.R
import com.example.cafebook.databinding.DialogNoNetworkBinding

class NoNetworkDialogFragment : DialogFragment() {
    private var _binding: DialogNoNetworkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogNoNetworkBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogTitle.text = getString(android.R.string.dialog_alert_title)
        binding.dialogMessage.text = getString(R.string.network_type_no_network)
        binding.positiveButton.text = getString(R.string.yes)

        binding.positiveButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
