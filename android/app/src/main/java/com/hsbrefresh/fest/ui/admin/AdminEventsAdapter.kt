package com.hsbrefresh.fest.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hsbrefresh.fest.R
import com.hsbrefresh.fest.data.model.Event
import com.hsbrefresh.fest.databinding.ItemAdminEventRowBinding
import com.hsbrefresh.fest.utils.CategoryHelper

class AdminEventsAdapter : ListAdapter<Event, AdminEventsAdapter.AdminEventViewHolder>(AdminEventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminEventViewHolder {
        val binding = ItemAdminEventRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdminEventViewHolder(
        private val binding: ItemAdminEventRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            val context = binding.root.context

            binding.tvAdminEventIcon.text = event.icon ?: "⚡"
            binding.tvAdminEventTitle.text = event.title
            binding.tvAdminEventCategory.text = event.category
            CategoryHelper.applyCategoryBadge(binding.tvAdminEventCategory, event.category)

            binding.tvAdminEventSchedule.text = "${event.eventDate} • ${event.eventTime}"
            binding.tvAdminEventVenue.text = event.venue
            binding.tvAdminOccupancy.text = "${event.registered} / ${event.capacity}"
            binding.tvAdminUtilizationPct.text = "${event.utilizationPct}%"

            // Requirement: Highlight events exceeding 90% capacity
            if (event.isNearCapacity) {
                binding.cardAdminEvent.setBackgroundResource(R.drawable.bg_card_danger)
                binding.tvAdminUtilizationPct.setTextColor(ContextCompat.getColor(context, R.color.rose_400))
            } else {
                binding.cardAdminEvent.setBackgroundResource(R.drawable.bg_card)
                if (event.utilizationPct > 50f) {
                    binding.tvAdminUtilizationPct.setTextColor(ContextCompat.getColor(context, R.color.amber_300))
                } else {
                    binding.tvAdminUtilizationPct.setTextColor(ContextCompat.getColor(context, R.color.slate_300))
                }
            }

            // Status badge
            if (event.isFull) {
                binding.tvAdminStatusBadge.setBackgroundResource(R.drawable.bg_badge_full)
                binding.tvAdminStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvAdminStatusBadge.text = context.getString(R.string.status_full)
            } else if (event.isNearCapacity) {
                binding.tvAdminStatusBadge.setBackgroundResource(R.drawable.bg_badge_seats_left)
                binding.tvAdminStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_300))
                binding.tvAdminStatusBadge.text = "Near Capacity"
            } else {
                binding.tvAdminStatusBadge.setBackgroundResource(R.drawable.bg_badge_gaming)
                binding.tvAdminStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_300))
                binding.tvAdminStatusBadge.text = "Available"
            }
        }
    }

    class AdminEventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem
    }
}
