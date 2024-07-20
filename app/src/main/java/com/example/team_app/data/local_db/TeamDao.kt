package com.example.team_app.data.local_db

import androidx.room.*
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers

// Data Access Object (DAO) for Team entity
@Dao
interface TeamDao {

    // Retrieves all teams along with their associated players. The @Transaction annotation ensures that the operation is atomic.
    @Transaction
    @Query("SELECT * FROM teams")
    suspend fun getAllTeamsWithPlayers(): List<TeamWithPlayers>

    // Inserts a team into the database. If a conflict occurs (e.g., the team already exists), it replaces the existing team.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    // Deletes a team from the database
    @Delete
    suspend fun delete(team: Team)

    // Updates an existing team in the database
    @Update
    suspend fun update(team: Team)
}
