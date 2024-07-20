package com.example.team_app.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.team_app.MyLifecycleObserver
import com.example.team_app.R
import com.example.team_app.data.model.Player
import com.example.team_app.databinding.AddPlayerLayoutBinding
import com.example.team_app.viewmodel.SharedViewModel

// Fragment for adding a new player
class AddPlayerFragment : Fragment() {

    // Binding for the layout
    private var _binding: AddPlayerLayoutBinding? = null
    private val binding get() = _binding!!

    // Observer for lifecycle events
    private lateinit var observer: MyLifecycleObserver

    // Launcher for speech recognition
    private val speechRecognizerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val spokenText =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                        ?: ""
                Log.d("AddEditPlayer", "Received spoken text: $spokenText")
                binding.editTextPlayerName.text =
                    Editable.Factory.getInstance().newEditable(spokenText)
            }
        }

    // Shared ViewModel for data communication between fragments
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Inflate the layout and initialize the binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddPlayerLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Set up the view after it is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the position spinner
        setupPositionSpinner()

        // Set click listener for the save button
        binding.buttonSavePlayer.setOnClickListener {
            val playerName = binding.editTextPlayerName.text.toString()
            val playerNumber = binding.editTextPlayerNumber.text.toString()
            val playerPosition = binding.spinnerPosition.selectedItem.toString()
            val playerAge = binding.editTextPlayerAge.text.toString()

            // Validate inputs and add the player if valid
            if (validateInputs(playerName, playerNumber, playerPosition, playerAge)) {
                val newPlayer = Player(
                    playerName = playerName,
                    playerNumber = playerNumber.toInt(),
                    playerPosition = playerPosition,
                    playerAge = playerAge.toInt(),
                    teamId = 0L // Temporary assignment
                )
                sharedViewModel.addPlayer(newPlayer)
                clearInputs()
                findNavController().navigate(R.id.action_addPlayerFragment_to_addEditTeamFragment2)
            }
        }

        // Add text change listeners to update the ViewModel
        binding.editTextPlayerName.addTextChangedListener {
            sharedViewModel.playerName.value = it.toString()
        }

        binding.editTextPlayerNumber.addTextChangedListener {
            sharedViewModel.playerNumber.value = it.toString()
        }

        binding.editTextPlayerAge.addTextChangedListener {
            sharedViewModel.playerAge.value = it.toString()
        }

        // Observe changes in the ViewModel and update the UI
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

        sharedViewModel.playerAge.observe(viewLifecycleOwner) { age ->
            if (binding.editTextPlayerAge.text.toString() != age) {
                binding.editTextPlayerAge.setText(age)
            }
        }

        sharedViewModel.selectedPosition.observe(viewLifecycleOwner) { position ->
            if (binding.spinnerPosition.selectedItemPosition != position) {
                binding.spinnerPosition.setSelection(position)
            }
        }

        // Initialize lifecycle observer
        observer = MyLifecycleObserver(
            requireActivity().activityResultRegistry,
            requireContext()
        )
        lifecycle.addObserver(observer)

        // Set click listener for the speech button
        binding.speechBtnPlayer.setOnClickListener {
            observer.checkPermission(Manifest.permission.RECORD_AUDIO) {
                val intent = sharedViewModel.getSpeechRecognizerIntent()
                speechRecognizerLauncher.launch(intent)
            }
        }

        // Observe speech result and update the player name
        sharedViewModel.speechResult.observe(viewLifecycleOwner) { result ->
            binding.editTextPlayerName.text = Editable.Factory.getInstance().newEditable(result)
        }
    }

    // Set up the position spinner with football positions
    private fun setupPositionSpinner() {
        val spinner: Spinner = binding.spinnerPosition
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.football_positions,
            R.layout.spinner_selected_item // Layout for the selected item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item) // Layout for the dropdown items
            spinner.adapter = adapter
        }

        // Update the ViewModel when a position is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != sharedViewModel.selectedPosition.value) {
                    sharedViewModel.selectedPosition.value = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed here
            }
        }
    }

    // Validate the inputs for adding a new player
    private fun validateInputs(
        name: String,
        number: String,
        position: String,
        age: String
    ): Boolean {
        if (name.isBlank() || number.isBlank() || position == getString(R.string.player_position) || age.isBlank()) {
            showToast(getString(R.string.all_fields_required))
            return false
        }

        if (!name.all { it.isLetter() || it.isWhitespace() }) {
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

        val playerAge = age.toIntOrNull()
        if (playerAge == null || playerAge !in 0..99) {
            showToast(getString(R.string.age_range))
            return false
        }

        return true
    }

    // Check if the text is in English
    private fun isEnglish(text: String): Boolean {
        return text.all { it.isLetter() && (it in 'A'..'Z' || it in 'a'..'z' || it.isWhitespace()) }
    }

    // Show a toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Clear the input fields and reset ViewModel data
    private fun clearInputs() {
        binding.editTextPlayerName.text?.clear()
        binding.editTextPlayerNumber.text?.clear()
        binding.editTextPlayerAge.text?.clear()
        binding.spinnerPosition.setSelection(0)
        sharedViewModel.selectedPosition.value = 0 // Reset ViewModel position as well
    }

    // Clean up the view binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
