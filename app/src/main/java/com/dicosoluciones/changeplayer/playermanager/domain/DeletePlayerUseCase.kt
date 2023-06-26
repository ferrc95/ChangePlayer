package com.dicosoluciones.changeplayer.playermanager.domain

import com.dicosoluciones.changeplayer.playermanager.data.di.PlayerRepository
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import javax.inject.Inject

class DeletePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerModel: PlayerModel) {
        return playerRepository.delete(playerModel)
    }
}