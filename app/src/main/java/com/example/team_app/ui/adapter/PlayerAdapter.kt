package com.example.team_app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_app.data.model.Player
import com.example.team_app.databinding.PlayerLayoutBinding

// Adapter class for displaying a list of players in a RecyclerView
class PlayerAdapter(private val players: MutableList<Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    // ViewHolder class to hold and bind the views for each player item
    class PlayerViewHolder(private val binding: PlayerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind the player data to the views
        fun bind(player: Player) {
            binding.textViewPlayerName.text = player.playerName
            binding.textViewPlayerNumber.text = player.playerNumber.toString()
        }
    }

    // Create a new ViewHolder instance for the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            PlayerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    // Bind the data to the ViewHolder for the given position
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    // Return the total number of items in the data set
    override fun getItemCount() = players.size

    // Update the list of players and notify the adapter of data changes
    fun updatePlayers(newPlayers: List<Player>) {
        val oldSize = players.size
        players.clear()
        players.addAll(newPlayers)

        // Notify the adapter of specific changes
        notifyItemRangeChanged(0, oldSize.coerceAtMost(newPlayers.size))
        if (newPlayers.size > oldSize) {
            notifyItemRangeInserted(oldSize, newPlayers.size - oldSize)
        } else if (newPlayers.size < oldSize) {
            notifyItemRangeRemoved(newPlayers.size, oldSize - newPlayers.size)
        }
    }
}
