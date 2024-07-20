package com.example.team_app.data.model

import androidx.room.Embedded
import androidx.room.Relation

// This class represents a relationship between a Team and its Players
data class TeamWithPlayers(
    // Embed the Team entity, meaning its fields will be directly available in this class
    @Embedded val team: Team,
    // Define the relationship between the Team and its Players
    @Relation(
        parentColumn = "teamId", // This is the ID of the team in the Team table
        entityColumn = "teamId" // This is the ID of the team in the Player table
    )
    val players: List<Player> // List of players associated with the team
)
