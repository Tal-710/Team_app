package com.example.team_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.team_app.R
import com.example.team_app.data.model.Player
import com.example.team_app.databinding.TeamLayoutBinding
import com.example.team_app.viewmodel.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TeamFragment : Fragment() {

    private var _binding: TeamLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TeamLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.chosenTeam.observe(viewLifecycleOwner, Observer { teamWithPlayers ->
            val team = teamWithPlayers.team
            binding.textViewTeamName.text = team.teamName
            Glide.with(requireContext()).load(team.teamLogoUri).into(binding.imageViewTeamLogo)

            binding.buttonTeamPlayers.setOnClickListener {
                showPlayersDialog(teamWithPlayers.players)
            }

            binding.buttonEditTeam.setOnClickListener {
                sharedViewModel.setEditTeam(teamWithPlayers)
                sharedViewModel.setEditMode() // Set edit mode to true
                findNavController().navigate(R.id.action_teamFragment_to_addEditTeamFragment2)
            }
        })
    }

    private fun showPlayersDialog(players: List<Player>) {
        val playerNames = players.map { it.playerName }.toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Players")
            .setItems(playerNames) { dialog, which ->
                // Handle player click
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
