package com.example.team_app.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team_app.R

public class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    var spinnerPos: Long? = 0
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val fontSizeKey = "font_size"
    private val darkModeKey = "dark_mode"
    private val backgroundColorKey = "background_color"

    private val _fontSize = MutableLiveData<Int>().apply {
       // value = preferences.getInt(fontSizeKey, R.style.Theme_Team_app_Medium)
    }
    val fontSize: LiveData<Int> = _fontSize

    var darkMode: Boolean = false

    private val _isDarkMode = MutableLiveData<Boolean>().apply {
        value = preferences.getBoolean(darkModeKey, false)
    }
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _backgroundColor = MutableLiveData<Int>().apply {
        value = preferences.getInt(backgroundColorKey, R.style.color_White)
    }
    val backgroundColor: LiveData<Int> = _backgroundColor

    fun setFontSize(size: Int) {
        preferences.edit().putInt(fontSizeKey, size).apply()
        _fontSize.value = size
    }

    fun toggleDarkMode(isEnabled: Boolean) {
        preferences.edit().putBoolean(darkModeKey, isEnabled).apply()
        _isDarkMode.value = isEnabled
        AppCompatDelegate.setDefaultNightMode(if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun setBackgroundColor(color: Int) {
        preferences.edit().putInt(backgroundColorKey, color).apply()
        _backgroundColor.value = color
    }
}
