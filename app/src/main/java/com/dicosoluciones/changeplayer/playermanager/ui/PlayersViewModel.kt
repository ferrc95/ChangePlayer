package com.dicosoluciones.changeplayer.playermanager.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicosoluciones.changeplayer.playermanager.domain.AddPlayerUseCase
import com.dicosoluciones.changeplayer.playermanager.domain.DeletePlayerUseCase
import com.dicosoluciones.changeplayer.playermanager.domain.GetPlayersUseCase
import com.dicosoluciones.changeplayer.playermanager.domain.UpdatePlayerUseCase
import com.dicosoluciones.changeplayer.playermanager.ui.state.PlayersUiState.Success
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import com.dicosoluciones.changeplayer.playermanager.ui.state.PlayersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val addPlayerUseCase: AddPlayerUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    getPlayersUseCase: GetPlayersUseCase
) : ViewModel() {

    val uiState: StateFlow<PlayersUiState> = getPlayersUseCase().map(::Success)
        .catch { PlayersUiState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayersUiState.Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _showEditPlayer = MutableLiveData<Boolean>()
    val showEditPlayer: LiveData<Boolean> = _showEditPlayer

    private val _showTeams = MutableLiveData<Boolean>()
    val showTeams: LiveData<Boolean> = _showTeams

    private val _playerEdit = MutableLiveData<PlayerModel>()
    val playerEdit: LiveData<PlayerModel> = _playerEdit

    //Es necesario inicializar todos los array
    private val _playerList = MutableLiveData<ArrayList<PlayerModel>>(arrayListOf())
    val playerList: LiveData<ArrayList<PlayerModel>> = _playerList

    private val _teamOne = MutableLiveData<ArrayList<PlayerModel>>(arrayListOf())
    val teamOne: LiveData<ArrayList<PlayerModel>> = _teamOne

    private val _teamTwo = MutableLiveData<ArrayList<PlayerModel>>(arrayListOf())
    val teamTwo: LiveData<ArrayList<PlayerModel>> = _teamTwo

    fun onDialogClose() {
        _showDialog.value = false
        _showEditPlayer.value = false
        _showTeams.value = false
    }


    fun onUpdatePlayer(playerModel: PlayerModel) {
        _playerEdit.value = playerModel
    }

    fun onPlayerCreated(name: String, stars: Int) {
        _showDialog.value = false

        viewModelScope.launch {
            addPlayerUseCase(PlayerModel(id = 0, name = name, stars = stars))
        }
    }

    fun onPlayerEdited(playerModel: PlayerModel) {
        _showEditPlayer.value = false

        viewModelScope.launch {
            updatePlayerUseCase(playerModel)
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onShowEditPlayer(){
        _showEditPlayer.value = true
    }


    fun onPlayerDelete(playerModel: PlayerModel) {
        _showEditPlayer.value = false

        viewModelScope.launch {
            deletePlayerUseCase(playerModel)
        }
    }

    fun addOrDeleteListPlayers(playerModel: PlayerModel) {

        if(_playerList.value?.contains(playerModel) == true) {
            _playerList.value?.remove(playerModel)

        } else {
            _playerList.value?.add(playerModel)
        }
    }

    fun onShowTeams() {
        _showTeams.value = true
    }
    fun createTeams() {

        //Limpiamos los equipos
        _teamOne.value?.clear()
        _teamTwo.value?.clear()

        var playersBronze: ArrayList<PlayerModel> = ArrayList(arrayListOf())
        var playersSilver: ArrayList<PlayerModel> = ArrayList(arrayListOf())
        var playersGold: ArrayList<PlayerModel> = ArrayList(arrayListOf())

        val random = Random(System.currentTimeMillis())

        _playerList.value?.forEach {
            if (it.stars == 1) {
                playersBronze.add(it)
            } else if (it.stars == 2) {
                playersSilver.add(it)
            } else if (it.stars == 3) {
                playersGold .add(it)
            }
        }

        var flag: Boolean = true
        var index:Int = 0

        while (playersBronze.isNotEmpty() || playersSilver.isNotEmpty() || playersGold.isNotEmpty()) {

            if (flag) {
                if(playersBronze.isNotEmpty()) {
                    index = random.nextInt(0, playersBronze.size)
                    _teamOne.value?.add(playersBronze[index])
                    playersBronze.removeAt(index)
                }
                if(playersSilver.isNotEmpty()) {
                    index = random.nextInt(0, playersSilver.size)
                    _teamOne.value?.add(playersSilver[index])
                    playersSilver.removeAt(index)
                }
                if(playersGold.isNotEmpty()) {
                    index = random.nextInt(0, playersGold.size)
                    _teamOne.value?.add(playersGold[index])
                    playersGold.removeAt(index)
                }
            } else {
                if(playersBronze.isNotEmpty()) {
                    index = random.nextInt(0, playersBronze.size)
                    _teamTwo.value?.add(playersBronze[index])
                    playersBronze.removeAt(index)
                }
                if(playersSilver.isNotEmpty()) {
                    index = random.nextInt(0, playersSilver.size)
                    _teamTwo.value?.add(playersSilver[index])
                    playersSilver.removeAt(index)
                }
                if(playersGold.isNotEmpty()) {
                    index = random.nextInt(0, playersGold.size)
                    _teamTwo.value?.add(playersGold[index])
                    playersGold.removeAt(index)
                }
            }
            flag = !flag
        }

        //Ordenamos los equipos segun su numero de estrellas
        _teamOne.value?.sortByDescending { it.stars }
        _teamTwo.value?.sortByDescending { it.stars }
    }
}