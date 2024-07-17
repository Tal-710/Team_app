package com.example.team_app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.team_app.data.model.Player
import com.example.team_app.data.model.Team
import com.example.team_app.data.model.TeamWithPlayers
import com.example.team_app.data.repository.PlayerRepository
import com.example.team_app.data.repository.TeamRepository
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    var darkMode = false
    var spinnerPos: Long = 0

    private val playerRepository = PlayerRepository(application)
    private val teamRepository = TeamRepository(application)


    val playerName = MutableLiveData<String>()
    val playerNumber = MutableLiveData<String>()
    val playerPosition = MutableLiveData<String>()
    val playerAge = MutableLiveData<String>()

    val teamName = MutableLiveData<String>()
    val teamLogoUri = MutableLiveData<Uri?>()
    val teamEmail = MutableLiveData<String>()

    private val _editTeam = MutableLiveData<TeamWithPlayers?>()

    private val _teamContactNumber = MutableLiveData<String>()
    val teamContactNumber: LiveData<String> get() = _teamContactNumber
    val editTeam: LiveData<TeamWithPlayers?> get() = _editTeam

    private val _playerList = MutableLiveData<List<Player>>()
    val playerList: LiveData<List<Player>> get() = _playerList

    private val _teamList = MutableLiveData<List<TeamWithPlayers>>()
    val teamList: LiveData<List<TeamWithPlayers>> get() = _teamList

    private val _chosenTeam = MutableLiveData<TeamWithPlayers>()
    val chosenTeam: LiveData<TeamWithPlayers> get() = _chosenTeam

    private val tempPlayerList = mutableListOf<Player>()

    private val _isEditMode = MutableLiveData<Boolean>()
    val isEditMode: LiveData<Boolean> get() = _isEditMode

    init {
        loadAllTeams()
        resetEditMode()
    }

    fun setContactNumber(number: String) {
        _teamContactNumber.value = number
    }

    fun clearPlayerList() {
        tempPlayerList.clear()
        _playerList.value = emptyList()
    }

    private fun loadAllTeams() {
        viewModelScope.launch {
            _teamList.value = teamRepository.getAllTeams()
        }
    }

    fun setEditMode() {
        _isEditMode.value = true
    }

    fun setChosenTeam(teamWithPlayers: TeamWithPlayers) {
        _chosenTeam.value = teamWithPlayers
    }

    fun addPlayer(player: Player) {
        tempPlayerList.add(player)
        _playerList.value = tempPlayerList
    }

    fun removePlayer(index: Int) {
        val player = tempPlayerList.removeAt(index)
        viewModelScope.launch {
            playerRepository.deletePlayer(player)
            _playerList.value = tempPlayerList
        }
    }

    fun saveTeam(team: Team) {
        viewModelScope.launch {
            val teamId = teamRepository.insertTeam(team)
            tempPlayerList.forEach { player ->
                player.teamId = teamId
                playerRepository.addPlayer(player)
            }
            clearPlayerList()
            resetEditMode()
            loadAllTeams()
        }
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.updateTeam(team)
            tempPlayerList.forEach { player ->
                player.teamId = team.teamId!!
                playerRepository.addPlayer(player)
            }
            resetEditMode()
            loadAllTeams()
        }
    }

    fun deleteTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.deleteTeam(team)
            loadAllTeams()
        }
    }

    fun isTeamNameUnique(teamName: String): Boolean {
        return _teamList.value?.none { it.team.teamName == teamName } == true
    }

    fun setEditTeam(teamWithPlayers: TeamWithPlayers) {
        _editTeam.value = teamWithPlayers
        teamName.value = teamWithPlayers.team.teamName
        teamLogoUri.value = Uri.parse(teamWithPlayers.team.teamLogoUri)
        tempPlayerList.clear()
        tempPlayerList.addAll(teamWithPlayers.players)
        _playerList.value = tempPlayerList
        _isEditMode.value = true
        _teamContactNumber.value = teamWithPlayers.team.teamContactNumber
    }

    fun resetEditMode() {
        _editTeam.value = null
        teamName.value = ""
        teamLogoUri.value = null
        _isEditMode.value = false
        teamEmail.value = ""
        _teamContactNumber.value = "No contact selected"
    }
}
