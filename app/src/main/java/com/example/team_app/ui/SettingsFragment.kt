package com.example.team_app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.databinding.SettingsLayoutBinding
import com.example.team_app.viewmodel.SharedViewModel


class SettingsFragment : Fragment(){

    private var _binding: SettingsLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = SettingsLayoutBinding.inflate(inflater,container,false)
        val spinnerTextSizeAdapter=ArrayAdapter.createFromResource(requireContext(), R.array.text_Size_Spinner,
            android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFontSize.adapter = spinnerTextSizeAdapter
        val spinnerBackgroundColorAdapter=ArrayAdapter.createFromResource(requireContext(), R.array.background_Color_Spinner,
            android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBackGroundColor.adapter = spinnerBackgroundColorAdapter

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch is "on" state
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedViewModel.darkMode=true
                showToast("Dark mode is On")
            } else {
                // Switch is "off" state
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedViewModel.darkMode=false
                showToast("Dark mode is OFF ")
            }
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchDarkMode.isChecked=sharedViewModel.darkMode
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val currentTextSize=sharedPref?.getInt("textSize", R.style.color_White)
        binding.spinnerBackGroundColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                if (parent != null) {
                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    val currentBackground =
                        sharedPref?.getInt("backgroundColor", R.style.color_White)
                    if (position == 1 &&
                        currentBackground != R.style.color_White)
                    {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("backgroundColor", R.style.color_White)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                    if (position==2 &&
                        currentBackground != R.style.color_Green)
                    {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("backgroundColor", R.style.color_Green)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                    if (position == 3 &&
                        currentBackground != R.style.color_Yellow)
                    {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("backgroundColor", R.style.color_Yellow)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                    if (position == 4 &&
                        currentBackground != R.style.color_Red)
                    {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("backgroundColor", R.style.color_Red)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                    if (position == 5 &&
                        currentBackground != R.style.color_Purple)
                    {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("backgroundColor", R.style.color_Purple)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    sharedViewModel.spinnerPos= parent.getItemIdAtPosition(position)
                }
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val currentTextSize = sharedPref?.getInt("textSize", R.style.Font_Small)
                if(position==1 ) {
                    if(currentTextSize != R.style.Font_Small) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("textSize", R.style.Font_Small)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }
                if(position==2 ) {
                    if(currentTextSize!=R.style.Font_Medium) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putInt("textSize", R.style.Font_Medium)
                                apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }
                if(position==3 ) {
                    if(currentTextSize!=R.style.Font_Large) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putInt("textSize", R.style.Font_Large)
                                apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding= null
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}