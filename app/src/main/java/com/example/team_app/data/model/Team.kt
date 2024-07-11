package com.example.team_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) var teamId: Long? = null,
    val teamName: String,
    val teamLogoUri: String?
)
