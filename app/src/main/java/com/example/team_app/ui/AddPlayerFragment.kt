package com.example.team_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.data.model.Player
import com.example.team_app.databinding.AddPlayerLayoutBinding
import com.example.team_app.viewmodel.SharedViewModel

class AddPlayerFragment : Fragment() {

    private var _binding: AddPlayerLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddPlayerLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSavePlayer.setOnClickListener {
            val playerName = binding.editTextPlayerName.text.toString()
            val playerNumber = binding.editTextPlayerNumber.text.toString()
            val playerPosition = binding.editTextPlayerPosition.text.toString()
            val playerAge = binding.editTextPlayerAge.text.toString()

            if (validateInputs(playerName, playerNumber, playerPosition, playerAge)) {
                val newPlayer = Player(
                    playerName = playerName,
                    playerNumber = playerNumber.toInt(),
                    playerPosition = playerPosition,
                    playerAge = playerAge.toInt(),
                    teamId = 0L // Temporary assignment
                )
                sharedViewModel.addPlayer(newPlayer)
                findNavController().navigate(R.id.action_addPlayerFragment_to_addEditTeamFragment2)
                clearInputs()
            }
        }

        binding.editTextPlayerName.addTextChangedListener {
            sharedViewModel.playerName.value = it.toString()
        }

        binding.editTextPlayerNumber.addTextChangedListener {
            sharedViewModel.playerNumber.value = it.toString()
        }

        binding.editTextPlayerPosition.addTextChangedListener {
            sharedViewModel.playerPosition.value = it.toString()
        }

        binding.editTextPlayerAge.addTextChangedListener {
            sharedViewModel.playerAge.value = it.toString()
        }

        // Observing changes in the ViewModel
        sharedViewModel.playerName.observe(viewLifecycleOwner) { name ->
            if (binding.editTextPlayerName.text.toString() != name) {
                binding.editTextPlayerName.setText(name)
            }
        }

        sharedViewModel.playerNumber.observe(viewLifecycleOwner) { number ->
            if (binding.editTextPlayerNumber.text.toString() != number) {
                binding.editTextPlayerNumber.setText(number)
            }
        }

        sharedViewModel.playerPosition.observe(viewLifecycleOwner) { position ->
            if (binding.editTextPlayerPosition.text.toString() != position) {
                binding.editTextPlayerPosition.setText(position)
            }
        }

        sharedViewModel.playerAge.observe(viewLifecycleOwner) { age ->
            if (binding.editTextPlayerAge.text.toString() != age) {
                binding.editTextPlayerAge.setText(age)
            }
        }
    }
    private fun isEnglish(text: String): Boolean {
        return text.all { it.isLetter() && it in 'A'..'Z' || it in 'a'..'z' }
    }


    private fun validateInputs(name: String, number: String, position: String, age: String): Boolean {
        if (name.isBlank() || number.isBlank() || position.isBlank() || age.isBlank()) {
            showToast(getString(R.string.all_fields_required))
            return false
        }

        if (!name.all { it.isLetter() }) {
            showToast(getString(R.string.name_only_letters))
            return false
        }

        if (isEnglish(name) && !name[0].isUpperCase()) {
            showToast(getString(R.string.player_name_capital))
            return false
        }

        if (name.length > 20) {
            showToast(getString(R.string.name_length))
            return false
        }

        val playerNumber = number.toIntOrNull()
        if (playerNumber == null || playerNumber !in 1..99) {
            showToast(getString(R.string.number_range))
            return false
        }

        if (sharedViewModel.playerList.value?.any { it.playerNumber == playerNumber } == true) {
            showToast(getString(R.string.number_unique))
            return false
        }

        if (position.length != 2 || !position.all { it.isLetter() }) {
            showToast(getString(R.string.position_length))
            return false
        }

        val playerAge = age.toIntOrNull()
        if (playerAge == null || playerAge !in 0..99) {
            showToast(getString(R.string.age_range))
            return false
        }

        return true
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearInputs() {
        binding.editTextPlayerName.text?.clear()
        binding.editTextPlayerNumber.text?.clear()
        binding.editTextPlayerPosition.text?.clear()
        binding.editTextPlayerAge.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
