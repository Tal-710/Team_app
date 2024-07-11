package com.example.team_app.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers

@Dao
interface TeamDao {

    @Transaction
    @Query("SELECT * FROM teams")
    fun getAllTeamsWithPlayers(): LiveData<List<TeamWithPlayers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Delete
    suspend fun delete(team: Team)

    @Update
    suspend fun update(team: Team)
}
