package com.example.team_app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions

// Fragment to display all teams
class AllTeamsFragment : Fragment() {

    // Binding for the layout
    private var _binding: AllTeamLayoutBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel for data communication between fragments
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Adapter for displaying teams
    private lateinit var teamAdapter: TeamAdapter

    // Gesture detector for handling touch events
    private lateinit var gestureDetector: GestureDetector

    // Firebase Functions instance for sending emails
    private lateinit var functions: FirebaseFunctions

    // Inflate the layout and initialize the binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AllTeamLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Initialize Firebase and Firebase Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { FirebaseApp.initializeApp(it) }
        functions = FirebaseFunctions.getInstance()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the team adapter and RecyclerView
        teamAdapter = TeamAdapter(mutableListOf())
        binding.recyclerViewTeams.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = teamAdapter
        }

        // Observe the team list and update the adapter when data changes
        sharedViewModel.teamList.observe(viewLifecycleOwner, Observer { teams ->
            teams?.let { teamAdapter.updateTeams(it) }
        })

        // Set click listener for the add team button
        binding.buttonAddTeam.setOnClickListener {
            sharedViewModel.resetEditMode()
            sharedViewModel.clearPlayerList()
            findNavController().navigate(R.id.action_allTeamsFragment_to_addEditTeamFragment2)
        }

        // Set click listener for the about button
        binding.buttonAbout.setOnClickListener {
            findNavController().navigate(R.id.action_allTeamsFragment_to_aboutFragment)
        }

        // Initialize gesture detector for handling single tap and long press
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
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

        // Set touch listener for the RecyclerView to handle gestures
        binding.recyclerViewTeams.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    // Show a confirmation dialog to delete a team
    private fun showDeleteConfirmationDialog(team: TeamWithPlayers) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_team))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_team))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                val teamName = team.team.teamName
                val teamEmail = team.team.teamEmail
                sendDeleteMailToUser(teamName, teamEmail)
                sharedViewModel.deleteTeam(team.team)
                dialog.dismiss()
            }.show()
    }

    // Send an email to the user when a team is deleted
    private fun sendDeleteMailToUser(teamName: String, userEmail: String) {
        val data = hashMapOf(
            "teamName" to teamName,
            "userEmail" to userEmail
        )
        functions.getHttpsCallable("sendDeleteMailToUser")
            .call(data)
            .addOnFailureListener { e ->
                Log.e("AllTeamsFragment", "Error sending email to user", e)
            }
    }

    // Clean up the view binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
