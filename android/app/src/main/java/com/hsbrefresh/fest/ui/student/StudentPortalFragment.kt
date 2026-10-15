package com.hsbrefresh.fest.ui.student

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsbrefresh.fest.R
import com.hsbrefresh.fest.data.network.ApiClient
import com.hsbrefresh.fest.databinding.FragmentStudentPortalBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentPortalFragment : Fragment() {

    private var _binding: FragmentStudentPortalBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private var currentCategory: String = "All"
    private var currentSearch: String = ""
    private var searchDebounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentPortalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryFilters()
        setupSearch()
        setupActions()

        fetchAndRenderEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val dialog = EventDetailBottomSheetDialogFragment.newInstance(event.id)
            dialog.onRegistrationSuccess = {
                // Instantly re-fetch events to update live seat count
                fetchAndRenderEvents()
            }
            dialog.show(parentFragmentManager, EventDetailBottomSheetDialogFragment.TAG)
        }

        binding.recyclerEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupCategoryFilters() {
        val filterButtons = listOf(
            binding.pillCatAll to "All",
            binding.pillCatTech to "Technical",
            binding.pillCatCultural to "Cultural",
            binding.pillCatSports to "Sports",
            binding.pillCatGaming to "Gaming",
            binding.pillCatWorkshop to "Workshop"
        )

        filterButtons.forEach { (view, category) ->
            view.setOnClickListener {
                if (currentCategory != category) {
                    currentCategory = category
                    updateCategoryUI(filterButtons.map { it.first }, view)
                    fetchAndRenderEvents()
                }
            }
        }
    }

    private fun updateCategoryUI(allPills: List<TextView>, activePill: TextView) {
        val activeBg = R.drawable.bg_pill_active
        val inactiveBg = R.drawable.bg_pill_inactive
        val activeColor = ContextCompat.getColor(requireContext(), R.color.white)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.slate_300)

        allPills.forEach { pill ->
            if (pill == activePill) {
                pill.setBackgroundResource(activeBg)
                pill.setTextColor(activeColor)
            } else {
                pill.setBackgroundResource(inactiveBg)
                pill.setTextColor(inactiveColor)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounceJob?.cancel()
                searchDebounceJob = lifecycleScope.launch {
                    delay(250) // 250ms debounce matching web app.js
                    currentSearch = s?.toString()?.trim() ?: ""
                    fetchAndRenderEvents()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupActions() {
        binding.swipeRefresh.setColorSchemeResources(R.color.brand_500, R.color.fuchsia_500)
        binding.swipeRefresh.setOnRefreshListener {
            fetchAndRenderEvents()
        }

        binding.btnRetry.setOnClickListener {
            fetchAndRenderEvents()
        }

        binding.btnResetFilters.setOnClickListener {
            binding.etSearch.text?.clear()
            currentSearch = ""
            currentCategory = "All"
            val allPills = listOf(
                binding.pillCatAll,
                binding.pillCatTech,
                binding.pillCatCultural,
                binding.pillCatSports,
                binding.pillCatGaming,
                binding.pillCatWorkshop
            )
            updateCategoryUI(allPills, binding.pillCatAll)
            fetchAndRenderEvents()
        }
    }

    fun fetchAndRenderEvents() {
        // Show loading state if swipe refresh is not active
        if (!binding.swipeRefresh.isRefreshing) {
            binding.layoutLoading.visibility = View.VISIBLE
        }
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(requireContext())
                val categoryParam = if (currentCategory == "All") null else currentCategory
                val searchParam = currentSearch.ifEmpty { null }

                val response = api.getEvents(category = categoryParam, search = searchParam)

                binding.layoutLoading.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!
                    if (events.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        eventAdapter.submitList(emptyList())
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        eventAdapter.submitList(events)
                    }
                } else {
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvErrorDetails.text = getString(R.string.error_server_subtext)
                }
            } catch (e: Exception) {
                binding.layoutLoading.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorDetails.text = "Connection error: ${e.localizedMessage ?: "Could not reach backend"}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): StudentPortalFragment = StudentPortalFragment()
    }
}
