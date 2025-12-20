package com.example.cafebook

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.cafe.ViewModels.BarcodeViewModel
import com.example.cafebook.databinding.FragmentBarcodeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class BarCodeFragment : Fragment() {
    private var _binding: FragmentBarcodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BarcodeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        setupListeners()
        observeViewModel()
        viewModel.loadLatestBarcode()

        return binding.root
    }

    private fun setupListeners() {
        binding.floatingActionButton.setOnClickListener {
            showInputDialog()
        }

        binding.light.setOnCheckedChangeListener { _, isChecked ->
            adjustBrightness(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.barcodeBitmap.observe(viewLifecycleOwner) { bitmap ->
            binding.imageBarcode.setImageBitmap(bitmap)
        }

        viewModel.barcodeText.observe(viewLifecycleOwner) { text ->
            binding.codeNum.text = text
        }
    }

    private fun adjustBrightness(enable: Boolean) {
        val layoutParams = requireActivity().window.attributes
        layoutParams.screenBrightness = if (enable) 1.0f else -1.0f
        requireActivity().window.attributes = layoutParams
    }

    private fun showInputDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_barcode, null)
        val dialog =
            Dialog(requireContext()).apply {
                setContentView(dialogBinding)
                setCancelable(true)
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }

        dialogBinding.findViewById<Button>(R.id.buttonlimit).setOnClickListener {
            val text = dialogBinding.findViewById<EditText>(R.id.editTextlimit).text.toString()
            if (text.isNotBlank()) {
                viewModel.saveBarcode(text)
                dialog.dismiss()
                Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.not_entered), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
