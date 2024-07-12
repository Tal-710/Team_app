package com.example.team_app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_app.R
import com.example.team_app.data.model.TeamWithPlayers
import com.example.team_app.databinding.AllTeamLayoutBinding
import com.example.team_app.ui.adapter.TeamAdapter
import com.example.team_app.viewmodel.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AllTeamsFragment : Fragment() {

    private var _binding: AllTeamLayoutBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllTeamLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        teamAdapter = TeamAdapter(mutableListOf())
        binding.recyclerViewTeams.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = teamAdapter
        }

        sharedViewModel.teamList.observe(viewLifecycleOwner, Observer { teams ->
            teams?.let { teamAdapter.updateTeams(it) }
        })

        binding.buttonAddTeam.setOnClickListener {
            sharedViewModel.resetEditMode()
            findNavController().navigate(R.id.action_allTeamsFragment_to_addEditTeamFragment2)
        }

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_allTeamsFragment_to_settingsFragment)
        }

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val view = binding.recyclerViewTeams.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val position = binding.recyclerViewTeams.getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        val team = teamAdapter.teams[position]
                        sharedViewModel.setChosenTeam(team)
                        findNavController().navigate(R.id.action_allTeamsFragment_to_teamFragment)
                    }
                }
                return super.onSingleTapUp(e)
            }

            override fun onLongPress(e: MotionEvent) {
                val view = binding.recyclerViewTeams.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val position = binding.recyclerViewTeams.getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        val team = teamAdapter.teams[position]
                        showDeleteConfirmationDialog(team)
                    }
                }
            }
        })

        binding.recyclerViewTeams.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun showDeleteConfirmationDialog(team: TeamWithPlayers) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Team")
            .setMessage("Are you sure you want to delete this team?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog, _ ->
                sharedViewModel.deleteTeam(team.team)
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
