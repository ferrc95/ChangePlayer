package com.dicosoluciones.changeplayer.playermanager.ui.state

import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel

sealed interface PlayersUiState {
    object Loading: PlayersUiState
    data class Error(val throwable: Throwable): PlayersUiState
    data class Success(val players:List<PlayerModel>): PlayersUiState
}