package com.hsbrefresh.fest.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hsbrefresh.fest.R
import com.hsbrefresh.fest.data.network.ApiClient
import com.hsbrefresh.fest.data.network.ApiConfig
import com.hsbrefresh.fest.databinding.DialogServerSettingsBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ServerSettingsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogServerSettingsBinding? = null
    private val binding get() = _binding!!

    var onSettingsSaved: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogServerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUrl = ApiConfig.getBaseUrl(requireContext())
        binding.etServerUrl.setText(currentUrl)

        setupPresets()
        setupActions()
    }

    private fun setupPresets() {
        binding.btnSettingsClose.setOnClickListener {
            dismiss()
        }

        binding.btnPresetEmulator.setOnClickListener {
            binding.etServerUrl.setText("http://10.0.2.2:8021/")
        }

        binding.btnPresetEmulator8082.setOnClickListener {
            binding.etServerUrl.setText("http://10.0.2.2:8082/")
        }

        binding.btnPresetLocalhost.setOnClickListener {
            binding.etServerUrl.setText("http://127.0.0.1:8082/")
        }

        binding.btnPresetCloud.setOnClickListener {
            binding.etServerUrl.setText("https://kotlin-android-event-portal-app.onrender.com/")
        }

        binding.btnPresetWifi.setOnClickListener {
            binding.etServerUrl.setText("http://172.16.53.144:8021/")
        }

        binding.btnPresetWifi8082.setOnClickListener {
            binding.etServerUrl.setText("http://172.16.53.144:8082/")
        }
    }

    private fun setupActions() {
        binding.btnTestConnection.setOnClickListener {
            testServerConnection()
        }

        binding.btnSaveServerSettings.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                ApiConfig.setBaseUrl(requireContext(), url)
                onSettingsSaved?.invoke()
                dismiss()
            }
        }
    }

    private fun testServerConnection() {
        val rawUrl = binding.etServerUrl.text.toString().trim()
        val url = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
        val testEndpoint = "${url}api/stats"

        binding.tvConnectionTestResult.visibility = View.VISIBLE
        binding.tvConnectionTestResult.text = "Testing connectivity to $testEndpoint..."
        binding.tvConnectionTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber_300))

        lifecycleScope.launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder().url(testEndpoint).build()
                val response = kotlinx.coroutines.Dispatchers.IO.let { dispatcher ->
                    kotlinx.coroutines.withContext(dispatcher) {
                        client.newCall(request).execute()
                    }
                }

                if (response.isSuccessful) {
                    binding.tvConnectionTestResult.text = "✅ Connection Successful! (HTTP ${response.code})"
                    binding.tvConnectionTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.emerald_400))
                } else {
                    binding.tvConnectionTestResult.text = "⚠️ Server responded with HTTP ${response.code}"
                    binding.tvConnectionTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.rose_300))
                }
            } catch (e: Exception) {
                binding.tvConnectionTestResult.text = "❌ Failed: ${e.localizedMessage ?: "Could not reach host"}"
                binding.tvConnectionTestResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.rose_400))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ServerSettingsDialogFragment"

        fun newInstance(): ServerSettingsDialogFragment {
            return ServerSettingsDialogFragment()
        }
    }
}
