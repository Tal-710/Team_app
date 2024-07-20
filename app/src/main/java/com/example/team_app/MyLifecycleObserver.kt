package com.example.team_app

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

// MyLifecycleObserver class to handle permission requests
class MyLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val context: Context
) : DefaultLifecycleObserver {

    // Launcher for requesting permissions
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    // Action to perform when permission is granted
    private var permissionGrantedAction: (() -> Unit)? = null

    // onCreate method to set up the permission launcher
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        // Register the permission launcher with the activity result registry
        requestPermissionLauncher = registry.register(
            "permissionKey", owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // If permission is granted, perform the action
                permissionGrantedAction?.invoke()
            } else {
                // If permission is denied, show a toast message
                Toast.makeText(
                    context,
                    context.getString(R.string.permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Method to check if permission is granted, and request it if not
    fun checkPermission(permission: String, onGranted: () -> Unit) {
        // Store the action to perform if permission is granted
        permissionGrantedAction = onGranted
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, request it
            requestPermissionLauncher.launch(permission)
        } else {
            // If permission is already granted, perform the action
            onGranted()
        }
    }
}
