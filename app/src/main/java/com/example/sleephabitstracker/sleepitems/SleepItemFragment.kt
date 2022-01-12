package com.example.sleephabitstracker.sleepitems

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.sleephabitstracker.MainViewModel
import com.example.sleephabitstracker.R

class SleepItemFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var listAdapter: SleepItemRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sleep_item_list, container, false)

        listAdapter = SleepItemRecyclerViewAdapter(mutableListOf())

        mainViewModel.getAllSleepEvents.observe(viewLifecycleOwner) { sleepEvents ->
            Log.d("SleepItemFragment", "sleepEvents: $sleepEvents")

            if (sleepEvents.isNotEmpty()) {
                var newItems = sleepEvents.map {
                    item -> SleepItem(item.startTimestamp, item.startTimestamp, item.endTimestamp)
                }
                listAdapter.updateSleepEvents(newItems)
            }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = listAdapter
            }
        }
        return view
    }

}