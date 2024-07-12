package com.example.team_app.data.local_db

import androidx.room.*
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers

@Dao
interface TeamDao {

    @Transaction
    @Query("SELECT * FROM teams")
    suspend fun getAllTeamsWithPlayers(): List<TeamWithPlayers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Delete
    suspend fun delete(team: Team)

    @Update
    suspend fun update(team: Team)
}
