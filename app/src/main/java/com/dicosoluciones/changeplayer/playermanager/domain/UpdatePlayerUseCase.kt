package com.dicosoluciones.changeplayer.playermanager.domain

import com.dicosoluciones.changeplayer.playermanager.data.di.PlayerRepository
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerModel: PlayerModel) {
        return playerRepository.update(playerModel)
    }
}