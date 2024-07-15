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
        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_allTeamsFragment)
        }
        val spinnerTextSizeAdapter=ArrayAdapter.createFromResource(requireContext(), R.array.text_Size_Spinner,
            android.R.layout.simple_spinner_item)
        binding.spinnerFontSize.adapter = spinnerTextSizeAdapter
        /*val spinnerBackgroundColorAdapter=ArrayAdapter.createFromResource(requireContext(), R.array.text_Size_Spinner,
            android.R.layout.simple_spinner_item)
        binding..adapter = spinnerBackgroundColorAdapter
         */
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
        //binding.spinnerFontSize.selectedItemId=sharedViewModel.spinnerPos
        binding.spinnerFontSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    sharedViewModel.spinnerPos= parent.getItemIdAtPosition(position)
                }
                else
                {
                    sharedViewModel.spinnerPos= 0
                }
                showToast(position.toString())
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                val currentTextSize = sharedPref?.getInt("textSize", R.style.Theme_Team_app_Small)
                if(position==0 ) {
                    if(currentTextSize != R.style.Theme_Team_app_Small) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                this.putInt("textSize", R.style.Theme_Team_app_Small)
                                this.apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }
                if(position==1 ) {
                    if(currentTextSize!=R.style.Theme_Team_app_Medium) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putInt("textSize", R.style.Theme_Team_app_Medium)
                                apply()
                            }
                        }
                        if (activity != null) {
                            recreate(activity!!)
                        }
                    }
                }
                if(position==2 ) {
                    if(currentTextSize!=R.style.Theme_Team_app_Large) {
                        if (sharedPref != null) {
                            with(sharedPref.edit()) {
                                putInt("textSize", R.style.Theme_Team_app_Large)
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
        // binding.spinnerFontSize.onItemClickListener to do add listner to change size of spinner
        //and add the value to sharedview


    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding= null
    }
private fun showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
}