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

        // Using TextWatchers to avoid using setText directly
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

    private fun validateInputs(name: String, number: String, position: String, age: String): Boolean {
        if (name.isBlank() || number.isBlank() || position.isBlank() || age.isBlank()) {
            showToast("All fields must be filled out.")
            return false
        }

        if (!name.all { it.isLetter() }) {
            showToast("Name can only contain letters.")
            return false
        }

        if (name.length > 20) {
            showToast("Name cannot be longer than 20 characters.")
            return false
        }

        val playerNumber = number.toIntOrNull()
        if (playerNumber == null || playerNumber !in 1..99) {
            showToast("Number must be between 1 and 99.")
            return false
        }

        if (sharedViewModel.playerList.value?.any { it.playerNumber == playerNumber } == true) {
            showToast("Player number must be unique.")
            return false
        }

        if (position.length != 2 || !position.all { it.isLetter() }) {
            showToast("Position must be exactly 2 letters.")
            return false
        }

        val playerAge = age.toIntOrNull()
        if (playerAge == null || playerAge !in 16..99) {
            showToast("Age must be between 16 and 99.")
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
