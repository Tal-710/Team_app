package com.example.team_app.data.repository

import android.app.Application
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.model.Player

// Repository class for managing player data
class PlayerRepository(application: Application) {

    // Get an instance of PlayerDao from the AppDatabase
    private val playerDao = AppDatabase.getDatabase(application).playerDao()

    // Function to add a player to the database
    suspend fun addPlayer(player: Player) {
        playerDao.addPlayer(player)
    }

    // Function to delete a player from the database
    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayer(player)
    }
}
