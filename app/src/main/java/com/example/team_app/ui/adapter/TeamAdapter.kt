package com.example.team_app.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_app.data.model.TeamWithPlayers
import com.example.team_app.databinding.ItemTeamLayoutBinding

class TeamAdapter(val teams: MutableList<TeamWithPlayers>) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(private val binding: ItemTeamLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(teamWithPlayers: TeamWithPlayers) {
            val team = teamWithPlayers.team
            binding.textViewTeamItemName.text = team.teamName
            binding.imageViewTeamItemLogo.setImageURI(team.teamLogoUri?.let { Uri.parse(it) })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount() = teams.size

    fun updateTeams(newTeams: List<TeamWithPlayers>) {
        teams.clear()
        teams.addAll(newTeams)
        notifyDataSetChanged()
    }
}

