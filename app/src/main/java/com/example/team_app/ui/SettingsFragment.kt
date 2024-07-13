package com.example.team_app.ui

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.databinding.SettingsLayoutBinding
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.example.team_app.ui.adapter.TeamAdapter
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
        val spinnerArrayAdapter=ArrayAdapter.createFromResource(requireContext(), R.array.text_Size_Spinner,
            android.R.layout.simple_spinner_item)
            binding.spinnerFontSize.adapter = spinnerArrayAdapter

        // binding.spinnerFontSize.onItemClickListener to do add listner to change size of spinner
        //and add the value to sharedview

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_allTeamsFragment)
        }
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


    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding= null
    }
private fun showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
}