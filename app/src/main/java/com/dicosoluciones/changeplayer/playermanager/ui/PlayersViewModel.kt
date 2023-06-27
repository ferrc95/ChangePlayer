package com.dicosoluciones.changeplayer.playermanager.ui

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

    private val _playerEdit = MutableLiveData<PlayerModel>()
    val playerEdit: LiveData<PlayerModel> = _playerEdit

    private val _playerList = MutableLiveData<ArrayList<PlayerModel>>()
    val playerList: LiveData<ArrayList<PlayerModel>> = _playerList

    fun onDialogClose() {
        _showDialog.value = false
        _showEditPlayer.value = false
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
            _playerList.value?.add(playerModel)
        } else {
            _playerList.value?.remove(playerModel)
        }

    }

}