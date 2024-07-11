package com.example.team_app.data.local_db

import android.util.Log
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
    fun addPlayer(player: Player)

    @Delete
    fun deletePlayer(vararg player: Player)

    @Query("SELECT * FROM PLAYERS ORDER BY playerNumber ASC")
    fun getPlayers() : LiveData<List<Player>>


    @Query("SELECT * FROM PLAYERS WHERE playerId LIKE :id")
    fun getPlayer(id:Long) : Player

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    fun getPlayersForTeam(teamId: Long): LiveData<List<Player>>


}