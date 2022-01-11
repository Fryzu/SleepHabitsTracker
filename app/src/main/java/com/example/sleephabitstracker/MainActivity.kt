package com.example.sleephabitstracker

import android.app.PendingIntent
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.sleephabitstracker.databinding.ActivityMainBinding
import com.example.sleephabitstracker.receivers.SleepEventsReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sleepPendingIntent: PendingIntent

    private val mainViewModel: MainViewModel by lazy {
        MainViewModel(application)
    }

    private var sleepEventsOutput: String = ""

    private var subscribedToSleepEvents = false
        set(newSubscribedToSleepData) {
            field = newSubscribedToSleepData
            if (newSubscribedToSleepData) {
                binding.buttonSubscriptionStatus.text = "Stop tracking"
            } else {
                binding.buttonSubscriptionStatus.text = "Start tracking"
            }
            updateOutput()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.subscribedToSleepEvents.observe(this) { newSubscribedToSleepData ->
            if (subscribedToSleepEvents != newSubscribedToSleepData) {
                subscribedToSleepEvents = newSubscribedToSleepData
            }
        }

        mainViewModel.getAllSleepEvents.observe(this) { sleepEvents ->
            Log.d("MainActivity", "sleepEvents: $sleepEvents")

            if (sleepEvents.isNotEmpty()) {
                // Constructor isn't accessible for [SleepSegmentEvent], so we just output the
                // database table version.
                sleepEventsOutput = sleepEvents.joinToString {
                    "\t$it\n"
                }
                updateOutput()
            }
        }

        sleepPendingIntent = SleepEventsReceiver.createSleepReceiverPendingIntent(applicationContext)
    }

    fun onClickRequestSleepData(view: View) {
        if (hasActivityRecognitionPermission()) {
            if (subscribedToSleepEvents) {
                unsubscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
            } else {
                subscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
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
            // Registers for both [SleepSegmentEvent] and [SleepClassifyEvent] data.
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

    private fun hasActivityRecognitionPermission(): Boolean {
        // Because this app targets 29 and above (recommendation for using the Sleep APIs), we
        // don't need to check if this is on a device before runtime permissions, that is, a device
        // prior to 29 / Q.
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private val requestActivityRecognitionPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Permission denied on Android platform that supports runtime permissions.
                displayPermissionSettingsSnackBar()
            } else {
                // Permission was granted (either by approval or Android version below Q).
                binding.buttonSubscriptionStatus.text = "Stop tracking"
            }
        }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.mainActivity,
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

    private fun updateOutput() {
        Log.d(TAG, "updateOutput()")
        binding.textViewOutput.text = sleepEventsOutput
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}