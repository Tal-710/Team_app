package com.example.team_app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.team_app.R
import com.example.team_app.data.model.Player
import com.example.team_app.databinding.TeamLayoutBinding
import com.example.team_app.viewmodel.SharedViewModel

class TeamFragment : Fragment() {

    private var _binding: TeamLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
//    private val callPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            call()
//        } else {
//            Toast.makeText(requireContext(), "Can't make call without permission", Toast.LENGTH_LONG).show()
//        }
//    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                sharedViewModel.setEditMode()
                findNavController().navigate(R.id.action_teamFragment_to_addEditTeamFragment2)


            }
//            binding.buttonCallContact.setOnClickListener {
//                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
//            }
        })
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showPlayersDialog(players: List<Player>) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_player_list_layout, null)

        val tableLayout = dialogView.findViewById<TableLayout>(R.id.tableLayoutPlayers)

        // Adding player data rows dynamically
        players.forEach { player ->
            val tableRow = TableRow(requireContext())

            val textViewName = TextView(requireContext()).apply {
                text = player.playerName
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }
            val textViewNumber = TextView(requireContext()).apply {
                text = player.playerNumber.toString()
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }
            val textViewPosition = TextView(requireContext()).apply {
                text = player.playerPosition
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }
            val textViewAge = TextView(requireContext()).apply {
                text = player.playerAge.toString()
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 16, 16, 16)
                textSize = 18f
            }

            tableRow.addView(textViewName)
            tableRow.addView(textViewNumber)
            tableRow.addView(textViewPosition)
            tableRow.addView(textViewAge)

            tableLayout.addView(tableRow)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }


//    private fun call() {
//        val phone = sharedViewModel.contactNumber.value
//        if (!phone.isNullOrEmpty()) {
//            val intent = Intent(Intent.ACTION_CALL).apply {
//                data = Uri.parse("tel:$phone")
//            }
//            startActivity(intent)
//        } else {
//            Toast.makeText(requireContext(), "No contact number available", Toast.LENGTH_LONG).show()
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
