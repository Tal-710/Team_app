package com.example.team_app.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_app.R
import com.example.team_app.data.model.Team
import com.example.team_app.databinding.AddEditTeamLayoutBinding
import com.example.team_app.ui.adapter.PlayerAdapter
import com.example.team_app.viewmodel.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions

class AddEditTeamFragment : Fragment() {

    private var _binding: AddEditTeamLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var playerAdapter: PlayerAdapter

    private var imageUri: Uri? = null
    private lateinit var functions: FirebaseFunctions

    // Variables to store initial data
    private var initialTeamName: String? = null
    private var initialTeamLogoUri: Uri? = null
    private var initialPlayerList: List<String>? = null
    private var hasChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { FirebaseApp.initializeApp(it) }
        functions = FirebaseFunctions.getInstance()
    }

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.imageViewTeamLogo.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(
                it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
            sharedViewModel.teamLogoUri.value = it
            checkForChanges()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.context?.let { FirebaseApp.initializeApp(it) }
        _binding = AddEditTeamLayoutBinding.inflate(inflater, container, false)
        binding.buttonSelectPhoto.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        functions = FirebaseFunctions.getInstance()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (hasChanges) {
                showUnsavedChangesDialog()
            } else {
                findNavController().navigateUp()
            }
        }

        playerAdapter = PlayerAdapter(mutableListOf())
        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        sharedViewModel.isEditMode.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode == true) {
                sharedViewModel.editTeam.value?.let { teamWithPlayers ->
                    val team = teamWithPlayers.team
                    binding.editTextTeamName.setText(team.teamName)
                    team.teamLogoUri?.let {
                        binding.imageViewTeamLogo.setImageURI(Uri.parse(it))
                    }
                    binding.editTextTeamEmail.setText(team.teamEmail)
                    binding.editTextTeamEmail.isEnabled = false // Disable editing in edit mode
                    playerAdapter.updatePlayers(teamWithPlayers.players)

                    // Store initial data
                    initialTeamName = team.teamName
                    initialTeamLogoUri = Uri.parse(team.teamLogoUri)
                    initialPlayerList = teamWithPlayers.players.map { it.playerName }
                }
            } else {
                // New team creation mode
                initialTeamName = null
                initialTeamLogoUri = null
                initialPlayerList = null
                binding.editTextTeamEmail.isEnabled = true // Enable editing in new team creation mode
            }
        }

        binding.editTextTeamEmail.addTextChangedListener {
            sharedViewModel.teamEmail.value = it.toString()
        }

        binding.editTextTeamName.addTextChangedListener {
            sharedViewModel.teamName.value = it.toString()
        }

        sharedViewModel.teamName.observe(viewLifecycleOwner) { name ->
            if (binding.editTextTeamName.text.toString() != name) {
                binding.editTextTeamName.setText(name)
            }
        }

        sharedViewModel.teamEmail.observe(viewLifecycleOwner) { email ->
            if (binding.editTextTeamEmail.text.toString() != email) {
                binding.editTextTeamEmail.setText(email)
            }
        }

        sharedViewModel.teamLogoUri.observe(viewLifecycleOwner) { uri ->
            binding.imageViewTeamLogo.setImageURI(uri)
        }

        sharedViewModel.playerList.observe(viewLifecycleOwner) { players ->
            if (players != null) {
                playerAdapter.updatePlayers(players)
            }
        }

        setupChangeTracking()

        binding.buttonAddPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_addEditTeamFragment2_to_addPlayerFragment)
        }

        binding.buttonSaveTeam.setOnClickListener {
            saveTeam()
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                showDeleteConfirmationDialog(position)
            }
        }).attachToRecyclerView(binding.recyclerViewPlayers)
    }

    private fun setupChangeTracking() {
        binding.editTextTeamName.addTextChangedListener {
            checkForChanges()
        }

        sharedViewModel.teamLogoUri.observe(viewLifecycleOwner) {
            checkForChanges()
        }

        sharedViewModel.playerList.observe(viewLifecycleOwner) { players ->
            playerAdapter.updatePlayers(players)
            checkForChanges()
        }
    }

    private fun checkForChanges() {
        val currentTeamName = binding.editTextTeamName.text.toString()
        val currentTeamLogoUri = sharedViewModel.teamLogoUri.value
        val currentPlayerList = sharedViewModel.playerList.value?.map { it.playerName }

        hasChanges = when {
            sharedViewModel.isEditMode.value == true -> (currentTeamName != initialTeamName
                    || currentTeamLogoUri != initialTeamLogoUri
                    || currentPlayerList != initialPlayerList)
            else -> (currentTeamName.isNotEmpty()
                    || currentTeamLogoUri != null
                    || currentPlayerList?.isNotEmpty() == true)
        }
    }

    private fun saveTeam() {
        val teamName = binding.editTextTeamName.text.toString()
        val teamLogoUri = sharedViewModel.teamLogoUri.value
        val players = sharedViewModel.playerList.value
        val teamEmail = binding.editTextTeamEmail.text.toString()

        if (teamName.isEmpty()) {
            showToast("Team name is required")
            return
        }

        if (!teamName[0].isUpperCase()) {
            showToast("Team name must start with a capital letter")
            return
        }

        if (teamName.length > 15) {
            showToast("Team name at most 15 characters long.")
            return
        }

        if (teamLogoUri == null) {
            showToast("Team logo is required")
            return
        }

        if (players.isNullOrEmpty()) {
            showToast("At least one player is required")
            return
        }

        if (!isValidEmail(teamEmail)) {
            showToast("Invalid email address")
            return
        }

        val team = Team(
            teamName = teamName,
            teamLogoUri = teamLogoUri.toString(),
            teamEmail = teamEmail
        )

        if (sharedViewModel.isEditMode.value == true) {
            team.teamId = sharedViewModel.editTeam.value?.team?.teamId
            sharedViewModel.updateTeam(team)
        } else {
            sharedViewModel.saveTeam(team)
            sendMailToUser(teamName, teamEmail)
        }

        clearInputFields()
        hasChanges = false
        findNavController().navigate(R.id.action_addEditTeamFragment2_to_allTeamsFragment)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showUnsavedChangesDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes. Are you sure you want to leave without saving?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Leave") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun clearInputFields() {
        binding.editTextTeamName.text?.clear()
        binding.imageViewTeamLogo.setImageURI(null)
        sharedViewModel.teamLogoUri.value = null
        sharedViewModel.teamName.value = ""
        playerAdapter.updatePlayers(emptyList())
        sharedViewModel.resetEditMode()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete this player?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                binding.recyclerViewPlayers.adapter!!.notifyItemChanged(position)
            }
            .setPositiveButton("OK") { dialog, _ ->
                sharedViewModel.removePlayer(position)
                binding.recyclerViewPlayers.adapter!!.notifyItemRemoved(position)
                dialog.dismiss()
            }.show()
    }

    private fun sendMailToAdmin(teamName: String) {
        val data = hashMapOf(
            "teamName" to teamName
        )
        functions.getHttpsCallable("sendMail")
            .call(data)
            .addOnSuccessListener {
                Log.d(TAG, "Email sent successfully to admin")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending email to admin", e)
            }
    }

    private fun sendMailToUser(teamName: String, userEmail: String) {
        val data = hashMapOf(
            "teamName" to teamName,
            "userEmail" to userEmail
        )
        functions.getHttpsCallable("sendMailToUser")
            .call(data)
            .addOnSuccessListener {
                Log.d(TAG, "Email sent successfully to user")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending email to user", e)
                showFailureToast()
            }
    }

    private fun showFailureToast() {
        view?.let { view ->
            Toast.makeText(view.context, "Failed to send email to user. Please check the email address and try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
