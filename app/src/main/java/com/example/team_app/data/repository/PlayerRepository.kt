package com.example.team_app.data.repository

import android.app.Application
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.model.Player


class PlayerRepository(application: Application) {

    private val playerDao = AppDatabase.getDatabase(application).playerDao()


    suspend fun addPlayer(player: Player) {
        playerDao.addPlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayer(player)
    }
}

