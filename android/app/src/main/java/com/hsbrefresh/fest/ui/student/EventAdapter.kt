package com.hsbrefresh.fest.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hsbrefresh.fest.R
import com.hsbrefresh.fest.data.model.Event
import com.hsbrefresh.fest.databinding.ItemEventCardBinding
import com.hsbrefresh.fest.utils.CategoryHelper

class EventAdapter(
    private val onEventClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(
        private val binding: ItemEventCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            val context = binding.root.context

            // Icon & Category
            binding.tvEventIcon.text = event.icon ?: "⚡"
            binding.tvEventCategory.text = event.category
            CategoryHelper.applyCategoryBadge(binding.tvEventCategory, event.category)

            // Fee indicator
            if (event.fee > 0) {
                binding.tvEventFee.text = context.getString(R.string.fee_prefix, event.fee)
                binding.tvEventFee.setTextColor(ContextCompat.getColor(context, R.color.amber_400))
            } else {
                binding.tvEventFee.text = context.getString(R.string.free_entry)
                binding.tvEventFee.setTextColor(ContextCompat.getColor(context, R.color.emerald_400))
            }

            // Title & Description
            binding.tvEventTitle.text = event.title
            binding.tvEventDescription.text = event.description ?: context.getString(R.string.hero_subtitle)

            // Metadata: Date/Time & Venue
            binding.tvEventSchedule.text = "${event.eventDate} • ${event.eventTime}"
            binding.tvEventVenue.text = event.venue

            // Seats badge styling (Matching web Tailwind states)
            if (event.isFull) {
                binding.tvSeatBadge.setBackgroundResource(R.drawable.bg_badge_full)
                binding.tvSeatBadge.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvSeatBadge.text = context.getString(R.string.status_full)
            } else if (event.availableSeats <= 10) {
                binding.tvSeatBadge.setBackgroundResource(R.drawable.bg_badge_seats_left)
                binding.tvSeatBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_300))
                binding.tvSeatBadge.text = context.getString(R.string.seats_left, event.availableSeats, event.capacity)
            } else {
                binding.tvSeatBadge.background = null
                binding.tvSeatBadge.setTextColor(ContextCompat.getColor(context, R.color.slate_400))
                binding.tvSeatBadge.text = context.getString(R.string.seats_count, event.availableSeats, event.capacity)
            }

            // Click listener
            binding.btnViewDetails.setOnClickListener {
                onEventClick(event)
            }
            binding.root.setOnClickListener {
                onEventClick(event)
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
