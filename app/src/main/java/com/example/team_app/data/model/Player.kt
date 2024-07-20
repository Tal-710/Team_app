package com.example.team_app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Define the Player entity with a foreign key relationship to the Team entity
@Entity(
    tableName = "players",
    foreignKeys = [ForeignKey(
        entity = Team::class,
        parentColumns = ["teamId"],
        childColumns = ["teamId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["teamId"])]
)
data class Player(
    // Primary key that auto-generates a unique ID for each player
    @PrimaryKey(autoGenerate = true) val playerId: Long = 0L,
    // Foreign key referencing the ID of the team the player belongs to
    var teamId: Long,
    val playerName: String,
    val playerNumber: Int,
    val playerPosition: String,
    val playerAge: Int
)
