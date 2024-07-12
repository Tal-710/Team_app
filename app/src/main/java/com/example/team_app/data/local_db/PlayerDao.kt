package com.example.team_app.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team_app.data.model.Player

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT * FROM PLAYERS ORDER BY playerNumber ASC")
    fun getPlayers(): LiveData<List<Player>>

    @Query("SELECT * FROM PLAYERS WHERE playerId LIKE :id")
    suspend fun getPlayer(id: Long): Player

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    suspend fun getPlayersByTeamId(teamId: Long): List<Player>
}
