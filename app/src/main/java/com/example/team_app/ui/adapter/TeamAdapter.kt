package com.example.team_app.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_app.data.model.TeamWithPlayers
import com.example.team_app.databinding.ItemTeamLayoutBinding

// Adapter class for displaying a list of teams in a RecyclerView
class TeamAdapter(val teams: MutableList<TeamWithPlayers>) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    // ViewHolder class to hold and bind the views for each team item
    class TeamViewHolder(private val binding: ItemTeamLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind the team data to the views
        fun bind(teamWithPlayers: TeamWithPlayers) {
            val team = teamWithPlayers.team
            binding.textViewTeamItemName.text = team.teamName
            // Set the team logo URI if available
            binding.imageViewTeamItemLogo.setImageURI(team.teamLogoUri.let { Uri.parse(it) })
        }
    }

    // Create a new ViewHolder instance for the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding =
            ItemTeamLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    // Bind the data to the ViewHolder for the given position
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    // Return the total number of items in the data set
    override fun getItemCount() = teams.size

    // Update the list of teams and notify the adapter of data changes
    fun updateTeams(newTeams: List<TeamWithPlayers>) {
        val oldSize = teams.size // Get the size of the old list
        teams.clear() // Clear the existing list of teams
        teams.addAll(newTeams) // Add the new list of teams

        // Notify the adapter of specific changes
        notifyItemRangeChanged(0, oldSize.coerceAtMost(newTeams.size))
        if (newTeams.size > oldSize) {
            notifyItemRangeInserted(oldSize, newTeams.size - oldSize)
        } else if (newTeams.size < oldSize) {
            notifyItemRangeRemoved(newTeams.size, oldSize - newTeams.size)
        }
    }
}
