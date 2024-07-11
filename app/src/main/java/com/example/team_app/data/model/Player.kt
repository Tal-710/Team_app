package com.example.team_app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [ForeignKey(
        entity = Team::class,
        parentColumns = ["teamId"],
        childColumns = ["teamId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Player(
    @PrimaryKey(autoGenerate = true) val playerId: Long = 0L,
    var teamId: Long,
    val playerName: String,
    val playerNumber: Int,
    val playerPosition: String,
    val playerAge: Int
)

