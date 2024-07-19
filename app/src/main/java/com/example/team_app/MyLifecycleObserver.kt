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


class MyLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val context: Context
) : DefaultLifecycleObserver {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var permissionGrantedAction: (() -> Unit)? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        requestPermissionLauncher = registry.register(
            "permissionKey", owner,
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                permissionGrantedAction?.invoke()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun checkPermission(permission: String, onGranted: () -> Unit) {
        permissionGrantedAction = onGranted
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(permission)
        } else {
            onGranted()
        }
    }

}
