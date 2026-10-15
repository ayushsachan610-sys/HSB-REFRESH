package com.hsbrefresh.fest.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.hsbrefresh.fest.data.model.ApiErrorResponse
import com.hsbrefresh.fest.data.model.CreateEventRequest
import com.hsbrefresh.fest.data.network.ApiClient
import com.hsbrefresh.fest.databinding.DialogAddEventBinding
import kotlinx.coroutines.launch

class AddEventDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAddEventBinding? = null
    private val binding get() = _binding!!

    var onEventCreated: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupListeners()
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Technical", "Cultural", "Sports", "Gaming", "Workshop")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        binding.spinnerNewCategory.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnAddModalClose.setOnClickListener {
            dismiss()
        }

        binding.btnSaveEvent.setOnClickListener {
            submitNewEvent()
        }
    }

    private fun submitNewEvent() {
        val title = binding.etNewTitle.text.toString().trim()
        val icon = binding.etNewIcon.text.toString().trim().ifEmpty { "🎪" }
        val category = binding.spinnerNewCategory.selectedItem?.toString() ?: "Technical"
        val venue = binding.etNewVenue.text.toString().trim()
        val date = binding.etNewDate.text.toString().trim()
        val time = binding.etNewTime.text.toString().trim()
        val capacityStr = binding.etNewCapacity.text.toString().trim()
        val feeStr = binding.etNewFee.text.toString().trim()
        val description = binding.etNewDescription.text.toString().trim().ifEmpty { null }

        // Client validation
        if (title.isEmpty() || venue.isEmpty() || date.isEmpty() || time.isEmpty() || capacityStr.isEmpty()) {
            showAlert("Please fill in all required fields marked with *")
            return
        }

        val capacity = capacityStr.toIntOrNull()
        if (capacity == null || capacity <= 0) {
            showAlert("Capacity must be a positive integer")
            return
        }

        val fee = feeStr.toIntOrNull() ?: 0

        binding.btnSaveEvent.isEnabled = false
        binding.btnSaveEvent.text = "Publishing..."

        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(requireContext())
                val request = CreateEventRequest(
                    title = title,
                    category = category,
                    venue = venue,
                    eventDate = date,
                    eventTime = time,
                    capacity = capacity,
                    fee = fee,
                    icon = icon,
                    description = description
                )

                val response = api.createEvent(request)
                if (response.isSuccessful && response.body() != null) {
                    onEventCreated?.invoke()
                    dismiss()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val parsed = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                        parsed.detail ?: "Failed to create event (${response.code()})"
                    } catch (e: Exception) {
                        "Failed to create event (${response.code()}): ${response.message()}"
                    }
                    showAlert(errorMessage)
                    binding.btnSaveEvent.isEnabled = true
                    binding.btnSaveEvent.text = "Save and Publish Event"
                }
            } catch (e: Exception) {
                showAlert("Network error: Could not connect to backend")
                binding.btnSaveEvent.isEnabled = true
                binding.btnSaveEvent.text = "Save and Publish Event"
            }
        }
    }

    private fun showAlert(msg: String) {
        binding.layoutAddEventAlert.visibility = View.VISIBLE
        binding.tvAddEventAlertMsg.text = msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddEventDialogFragment"

        fun newInstance(): AddEventDialogFragment {
            return AddEventDialogFragment()
        }
    }
}
