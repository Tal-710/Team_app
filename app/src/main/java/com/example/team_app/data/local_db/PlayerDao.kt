package com.example.team_app.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team_app.data.model.Player

// Data Access Object (DAO) for Player entity
@Dao
interface PlayerDao {

    // Insert a player into the database. If the player already exists, ignore the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlayer(player: Player)

    // Delete a player from the database
    @Delete
    suspend fun deletePlayer(player: Player)
}
