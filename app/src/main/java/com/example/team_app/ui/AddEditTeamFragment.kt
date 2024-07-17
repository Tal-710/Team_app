package com.example.team_app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
    private var initialTeamContactNumber: String? = null
    private var hasChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { FirebaseApp.initializeApp(it) }
        functions = FirebaseFunctions.getInstance()
    }

    @SuppressLint("Range")
    private val pickContactLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val contactUri: Uri? = result.data!!.data
            if (contactUri != null) {
                val cursor = requireContext().contentResolver.query(
                    contactUri,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    null,
                    null,
                    null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val contactNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        sharedViewModel.setContactNumber(contactNumber)
                        Toast.makeText(requireContext(), "Contact chosen: $contactNumber", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "No contact number found")
                        Toast.makeText(requireContext(), "No contact number found", Toast.LENGTH_SHORT).show()
                    }
                    it.close()
                }
            }
        }
    }

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.imageViewTeamLogo.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(
                it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
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
        binding.buttonAddTeamContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            pickContactLauncher.launch(intent)
        }

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
                    initialTeamContactNumber = team.teamContactNumber
                    binding.textViewTeamContactNumber.text = initialTeamContactNumber
                }
            } else {
                // New team creation mode
                initialTeamName = null
                initialTeamLogoUri = null
                initialPlayerList = null
                initialTeamContactNumber = null
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

        sharedViewModel.teamContactNumber.observe(viewLifecycleOwner) { contactNumber ->
            binding.textViewTeamContactNumber.text = contactNumber
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
        sharedViewModel.teamContactNumber.observe(viewLifecycleOwner) { contactNumber ->
            binding.textViewTeamContactNumber.text = contactNumber
            checkForChanges()
        }
    }

    private fun checkForChanges() {
        val currentTeamName = binding.editTextTeamName.text.toString()
        val currentTeamLogoUri = sharedViewModel.teamLogoUri.value
        val currentPlayerList = sharedViewModel.playerList.value?.map { it.playerName }
        val currentTeamContactNumber = sharedViewModel.teamContactNumber.value

        hasChanges = when {
            sharedViewModel.isEditMode.value == true -> (currentTeamName != initialTeamName
                    || currentTeamLogoUri != initialTeamLogoUri
                    || currentPlayerList != initialPlayerList
                    || currentTeamContactNumber != initialTeamContactNumber)
            else -> (currentTeamName.isNotEmpty()
                    || currentTeamLogoUri != null
                    || currentPlayerList?.isNotEmpty() == true
                    || currentTeamContactNumber?.isNotEmpty() == true)
        }
    }

    private fun saveTeam() {
        val teamName = binding.editTextTeamName.text.toString()
        val teamLogoUri = sharedViewModel.teamLogoUri.value
        val players = sharedViewModel.playerList.value
        val teamEmail = binding.editTextTeamEmail.text.toString()
        val teamContactNumber = sharedViewModel.teamContactNumber.value ?: ""

        if (teamName.isEmpty()) {
            showToast(getString(R.string.team_name_required))
            return
        }
        if (!sharedViewModel.isEditMode.value!! || initialTeamName != teamName) {
            if (!sharedViewModel.isTeamNameUnique(teamName)) {
                showToast(getString(R.string.team_name_unique))
                return
            }
        }


        if (isEnglish(teamName) && !teamName[0].isUpperCase()) {
            showToast(getString(R.string.team_name_capital))
            return
        }

        if (teamName.length > 16) {
            showToast(getString(R.string.team_name_length))
            return
        }

        if (teamLogoUri == null) {
            showToast(getString(R.string.team_logo_required))
            return
        }

        if (players.isNullOrEmpty()) {
            showToast(getString(R.string.player_required))
            return
        }

        if (!isValidEmail(teamEmail)) {
            showToast(getString(R.string.invalid_email))
            return
        }

        if (teamContactNumber == "No contact selected"){
            showToast(getString(R.string.team_contact_required))
            return
        }


        // Disable email editing if not in edit mode
        if (!sharedViewModel.isEditMode.value!!) {
            binding.editTextTeamEmail.isEnabled = false
        }

        val team = Team(
            teamName = teamName,
            teamLogoUri = teamLogoUri.toString(),
            teamEmail = teamEmail,
            teamContactNumber = teamContactNumber
        )

        if (sharedViewModel.isEditMode.value == true) {
            team.teamId = sharedViewModel.editTeam.value?.team?.teamId
            sharedViewModel.updateTeam(team)
            if (hasChanges) {
                sendMailToUser(teamName, teamEmail, true)
            }
        } else {
            sharedViewModel.saveTeam(team)
            sendMailToUser(teamName, teamEmail, false)
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
            .setTitle(getString(R.string.unsaved_changes_title))
            .setMessage(getString(R.string.unsaved_changes_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.leave)) { dialog, _ ->
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
        sharedViewModel.setContactNumber("")
        playerAdapter.updatePlayers(emptyList())
        sharedViewModel.resetEditMode()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_player_title))
            .setMessage(getString(R.string.delete_player_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                binding.recyclerViewPlayers.adapter!!.notifyItemChanged(position)
            }
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                sharedViewModel.removePlayer(position)
                binding.recyclerViewPlayers.adapter!!.notifyItemRemoved(position)
                dialog.dismiss()
            }.show()
    }

    private fun sendMailToUser(teamName: String, userEmail: String, isEditMode: Boolean) {
        val data = hashMapOf(
            "teamName" to teamName,
            "userEmail" to userEmail,
            "isEditMode" to isEditMode
        )
        functions.getHttpsCallable("sendMailToUser")
            .call(data)
            .addOnSuccessListener {
                Log.d(TAG, "Email sent successfully to user")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending email to user", e)
            }
    }

    private fun isEnglish(text: String): Boolean {
        return text.all { it.isLetter() && it in 'A'..'Z' || it in 'a'..'z' }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
