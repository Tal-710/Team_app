package com.example.team_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.databinding.SettingsLayoutBinding
import androidx.appcompat.app.AppCompatDelegate

class SettingsFragment : Fragment(){

    private var _binding: SettingsLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = SettingsLayoutBinding.inflate(inflater,container,false)

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_allTeamsFragment)
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Switch is "on" state
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    showToast("Dark mode is On")
                } else {
                    // Switch is "off" state
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    showToast("Dark mode is OFF ")
                }
            }

        return binding.root
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding= null
    }
private fun showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
}