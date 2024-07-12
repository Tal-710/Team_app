package com.example.team_app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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

class AddEditTeamFragment : Fragment() {

    private var _binding: AddEditTeamLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var playerAdapter: PlayerAdapter

    private var imageUri: Uri? = null

    val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.imageViewTeamLogo.setImageURI(it)
            requireActivity().contentResolver.takePersistableUriPermission(
                it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageUri = it
            sharedViewModel.teamLogoUri.value = it
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddEditTeamLayoutBinding.inflate(inflater, container, false)
        binding.buttonSelectPhoto.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerAdapter = PlayerAdapter(mutableListOf())
        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        sharedViewModel.isEditMode.observe(viewLifecycleOwner, Observer { isEditMode ->
            if (isEditMode == true) {
                sharedViewModel.editTeam.value?.let { teamWithPlayers ->
                    val team = teamWithPlayers.team
                    binding.editTextTeamName.setText(team.teamName)
                    team.teamLogoUri?.let {
                        binding.imageViewTeamLogo.setImageURI(Uri.parse(it))
                    }
                    playerAdapter.updatePlayers(teamWithPlayers.players)
                }
            }
        })

        sharedViewModel.teamName.observe(viewLifecycleOwner, Observer { name ->
            if (binding.editTextTeamName.text.toString() != name) {
                binding.editTextTeamName.setText(name)
            }
        })

        sharedViewModel.teamLogoUri.observe(viewLifecycleOwner, Observer { uri ->
            binding.imageViewTeamLogo.setImageURI(uri)
        })

        sharedViewModel.playerList.observe(viewLifecycleOwner, Observer { players ->
            if (players != null) {
                playerAdapter.updatePlayers(players)
            }
        })

        binding.buttonAddPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_addEditTeamFragment2_to_addPlayerFragment)
        }

        binding.buttonSaveTeam.setOnClickListener {
            saveTeam()
        }

        binding.editTextTeamName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sharedViewModel.teamName.value = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

    private fun saveTeam() {
        val teamName = binding.editTextTeamName.text.toString()
        val teamLogoUri = sharedViewModel.teamLogoUri.value
        val players = sharedViewModel.playerList.value

        if (teamName.isEmpty()) {
            showToast("Team name is required")
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

        val team = Team(
            teamName = teamName,
            teamLogoUri = teamLogoUri.toString()
        )

        if (sharedViewModel.isEditMode.value == true) {
            team.teamId = sharedViewModel.editTeam.value?.team?.teamId
            sharedViewModel.updateTeam(team)
        } else {
            sharedViewModel.saveTeam(team)
        }

        clearInputFields()
        findNavController().navigate(R.id.action_addEditTeamFragment2_to_allTeamsFragment)
    }

    private fun clearInputFields() {
        binding.editTextTeamName.text?.clear()
        binding.imageViewTeamLogo.setImageURI(null)
        sharedViewModel.teamLogoUri.value = null
        sharedViewModel.teamName.value = "" // Clear the player list in the view model
        playerAdapter.updatePlayers(emptyList()) // Clear the player list in the adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}