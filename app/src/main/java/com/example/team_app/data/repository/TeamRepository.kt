package com.example.team_app.data.repository

import android.app.Application
import com.example.team_app.data.local_db.AppDatabase
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers

// Repository class for managing team data
class TeamRepository(application: Application) {

    // Get an instance of TeamDao from the AppDatabase
    private val teamDao = AppDatabase.getDatabase(application).teamDao()

    // Function to get all teams along with their players
    suspend fun getAllTeams(): List<TeamWithPlayers> {
        return teamDao.getAllTeamsWithPlayers()
    }

    // Function to insert a new team into the database
    suspend fun insertTeam(team: Team): Long {
        return teamDao.insert(team)
    }

    // Function to update an existing team in the database
    suspend fun updateTeam(team: Team) {
        teamDao.update(team)
    }

    // Function to delete an existing team from the database
    suspend fun deleteTeam(team: Team) {
        teamDao.delete(team)
    }
}
