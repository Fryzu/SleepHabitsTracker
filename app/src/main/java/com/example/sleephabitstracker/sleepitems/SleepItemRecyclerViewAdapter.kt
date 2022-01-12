package com.example.sleephabitstracker.sleepitems

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.sleephabitstracker.databinding.SleepItemBinding

class SleepItemRecyclerViewAdapter(
        private val values: MutableList<SleepItem>)
    : RecyclerView.Adapter<SleepItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    return ViewHolder(SleepItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.startDate.text = SleepItem.getDateStrFromUnix(item.startTimestamp)
        holder.endDate.text = SleepItem.getDateStrFromUnix(item.endTimestamp)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: SleepItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val startDate: TextView = binding.startDate
        val endDate: TextView = binding.endDate

        override fun toString(): String {
            return super.toString() + " '" + startDate.text + "'"
        }
    }

    fun updateSleepEvents(events: List<SleepItem>) {
        values.clear()
        values.addAll(0, events)
        notifyDataSetChanged()
    }

}