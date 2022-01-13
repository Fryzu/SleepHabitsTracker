package com.example.sleephabitstracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.sleephabitstracker.databinding.FragmentTrackingButtonBinding
import com.example.sleephabitstracker.receivers.SleepEventsReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.material.snackbar.Snackbar

private const val TAG = "TrackingButtonFragment"

class TrackingButton : Fragment() {

    private lateinit var binding: FragmentTrackingButtonBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var sleepPendingIntent: PendingIntent

    private var subscribedToSleepEvents = false
        set(newSubscribedToSleepData) {
            field = newSubscribedToSleepData
            if (newSubscribedToSleepData) {
                binding.button.text = "Stop tracking"
            } else {
                binding.button.text = "Start tracking"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentTrackingButtonBinding.inflate(inflater, container, false)

        mainViewModel.subscribedToSleepEvents.observe(viewLifecycleOwner) { newSubscribedToSleepData ->
            if (subscribedToSleepEvents != newSubscribedToSleepData) {
                subscribedToSleepEvents = newSubscribedToSleepData
            }
        }
        sleepPendingIntent = SleepEventsReceiver.createSleepReceiverPendingIntent(requireContext())
        binding.button.setOnClickListener {
            view -> onClickRequestSleepData(view)
        }

        return binding.root
    }

    fun onClickRequestSleepData(view: View) {
        if (hasActivityRecognitionPermission()) {
            if (subscribedToSleepEvents) {
                unsubscribeToSleepSegmentUpdates(requireContext(), sleepPendingIntent)
            } else {
                subscribeToSleepSegmentUpdates(requireContext(), sleepPendingIntent)
            }
        } else {
            requestActivityRecognitionPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "requestSleepSegmentUpdates()")
        val task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
            pendingIntent,
            SleepSegmentRequest.getDefaultSleepSegmentRequest()
        )

        task.addOnSuccessListener {
            mainViewModel.updateSubscribedToSleepEvents(true)
            Log.d(TAG, "Successfully subscribed to sleep data.")
        }
        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when subscribing to sleep data: $exception")
        }
    }

    private fun unsubscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "unsubscribeToSleepSegmentUpdates()")
        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(pendingIntent)

        task.addOnSuccessListener {
            mainViewModel.updateSubscribedToSleepEvents(false)
            Log.d(TAG, "Successfully unsubscribed to sleep data.")
        }
        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when unsubscribing to sleep data: $exception")
        }
    }

    // Permissions ================================================================================

    private val requestActivityRecognitionPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                displayPermissionSettingsSnackBar()
            } else {
                binding.button.text = "Stop tracking"
            }
        }

    private fun hasActivityRecognitionPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.root,
            "This app cannot work without Activity Recognition Permission.",
            Snackbar.LENGTH_LONG
        )
            .setAction("Settings") {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }
}