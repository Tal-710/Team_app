package com.example.team_app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) var teamId: Long? = null,
    @ColumnInfo(name = "teamName") val teamName: String,
    @ColumnInfo(name = "teamLogoUri") val teamLogoUri: String,
    @ColumnInfo(name = "teamEmail") val teamEmail: String,
    @ColumnInfo(name = "teamContactNumber") val teamContactNumber: String

)
