package com.example.team_app.data.repository

import android.app.Application
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers


class TeamRepository(application: Application) {

    private val teamDao = AppDatabase.getDatabase(application).teamDao()

    suspend fun getAllTeams(): List<TeamWithPlayers> {
        return teamDao.getAllTeamsWithPlayers()
    }

    suspend fun insertTeam(team: Team): Long {
        return teamDao.insert(team)
    }


    suspend fun updateTeam(team: Team) {
        teamDao.update(team)
    }

    suspend fun deleteTeam(team: Team) {
            teamDao.delete(team)

    }
}

