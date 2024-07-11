package com.example.team_app.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeamRepository(application: Application) {

    private val teamDao = AppDatabase.getDatabase(application).teamDao()

    fun getAllTeams(): LiveData<List<TeamWithPlayers>> {
        return teamDao.getAllTeamsWithPlayers()
    }

    suspend fun insertTeam(team: Team): Long {
        return withContext(Dispatchers.IO) {
            teamDao.insert(team)
        }
    }

    suspend fun updateTeam(team: Team) {
        withContext(Dispatchers.IO) {
            teamDao.update(team)
        }
    }

    suspend fun deleteTeam(team: Team) {
        withContext(Dispatchers.IO) {
            teamDao.delete(team)
        }
    }
}
