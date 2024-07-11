package com.example.team_app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TeamWithPlayers(
    @Embedded val team: Team,
    @Relation(
        parentColumn = "teamId",
        entityColumn = "teamId"
    )
    val players: List<Player>
)
