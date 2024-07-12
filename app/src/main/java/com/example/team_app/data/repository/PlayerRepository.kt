package com.example.team_app.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.local_db.PlayerDao
import com.example.team_app.data.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerRepository(application: Application) {

    private var playerDao: PlayerDao?

    init {
        val db = AppDatabase.getDatabase(application.applicationContext)
        playerDao = db.playerDao()
    }

    suspend fun getPlayersByTeamId(teamId: Long): List<Player> {
        return withContext(Dispatchers.IO) {
            playerDao?.getPlayersByTeamId(teamId)!!
        }
    }

    suspend fun addPlayer(player: Player) {
        withContext(Dispatchers.IO) {
            playerDao?.addPlayer(player)
        }
    }

    suspend fun deletePlayer(player: Player) {
        withContext(Dispatchers.IO) {
            playerDao?.deletePlayer(player)
        }
    }

    fun getPlayers(): LiveData<List<Player>>? {
        return playerDao?.getPlayers()
    }

    fun getPlayer(id: Long) = playerDao?.getPlayer(id)
}
