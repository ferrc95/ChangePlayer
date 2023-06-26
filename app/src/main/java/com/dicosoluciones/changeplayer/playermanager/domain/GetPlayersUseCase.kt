package com.dicosoluciones.changeplayer.playermanager.domain

import com.dicosoluciones.changeplayer.playermanager.data.di.PlayerRepository
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayersUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<PlayerModel>> {
        return playerRepository.players
    }
}