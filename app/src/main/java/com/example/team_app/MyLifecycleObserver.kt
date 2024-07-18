package com.example.team_app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale


class MyLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val activity: Activity,
    private val onSpeechResult: (String) -> Unit
) : DefaultLifecycleObserver {

    private lateinit var speechActivityForResult: ActivityResultLauncher<Intent>
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
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        speechActivityForResult = registry.register("key",
            owner,
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val spokenText =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
                onSpeechResult(spokenText)
            }
        }
    }

    fun startSpeechRecognition() {
        checkPermission(Manifest.permission.RECORD_AUDIO) {
            launchSpeechRecognizer()
        }
    }

    fun checkPermission(permission: String, onGranted: () -> Unit) {
        permissionGrantedAction = onGranted
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        } else {
            onGranted()
        }
    }

    fun launchSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        }
        speechActivityForResult.launch(intent)
    }
}
