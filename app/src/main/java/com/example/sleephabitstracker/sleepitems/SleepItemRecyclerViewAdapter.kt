package com.example.sleephabitstracker.sleepitems

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.sleephabitstracker.databinding.SleepItemBinding
import java.time.Duration
import java.time.format.DateTimeFormatter

class SleepItemRecyclerViewAdapter(
        private val values: MutableList<SleepItem>)
    : RecyclerView.Adapter<SleepItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SleepItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private var hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private var dateFormatter = DateTimeFormatter.ofPattern("dd LLL uuuu")

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        val startDate = SleepItem.getDateObjectFromUnix(item.startTimestamp)
        val endDate = SleepItem.getDateObjectFromUnix(item.endTimestamp)
        val duration = Duration.between(startDate, endDate)

        holder.startTime.text = "Start time: ${startDate.format(hourFormatter)}"
        holder.endTime.text = "End time: ${endDate.format(hourFormatter)}"
        holder.sleep_duration.text = "${duration.toHours()}h"
        holder.startDate.text = startDate.format(dateFormatter)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: SleepItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val startDate: TextView = binding.startDate
        val endTime: TextView = binding.endTime
        val startTime: TextView = binding.startTime
        val sleep_duration: TextView = binding.sleepDuration

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