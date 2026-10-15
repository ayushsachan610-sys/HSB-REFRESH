package com.hsbrefresh.fest.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hsbrefresh.fest.data.model.Registration
import com.hsbrefresh.fest.databinding.ItemAdminRegistrationRowBinding
import com.hsbrefresh.fest.utils.DateHelper

class AdminRegistrationsAdapter(
    private val onCancelClick: (Registration) -> Unit
) : ListAdapter<Registration, AdminRegistrationsAdapter.AdminRegistrationViewHolder>(AdminRegDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRegistrationViewHolder {
        val binding = ItemAdminRegistrationRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminRegistrationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminRegistrationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdminRegistrationViewHolder(
        private val binding: ItemAdminRegistrationRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reg: Registration) {
            binding.tvRegId.text = "#${reg.id}"
            binding.tvRegStudentName.text = reg.studentName
            binding.tvRegRoll.text = reg.rollNumber
            binding.tvRegEventTitle.text = reg.eventTitle
            binding.tvRegEmail.text = reg.email
            binding.tvRegPhone.text = reg.phone ?: "—"
            binding.tvRegTimestamp.text = DateHelper.formatIsoTimestamp(reg.registeredAt)

            binding.btnCancelReg.setOnClickListener {
                onCancelClick(reg)
            }
        }
    }

    class AdminRegDiffCallback : DiffUtil.ItemCallback<Registration>() {
        override fun areItemsTheSame(oldItem: Registration, newItem: Registration): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Registration, newItem: Registration): Boolean = oldItem == newItem
    }
}
