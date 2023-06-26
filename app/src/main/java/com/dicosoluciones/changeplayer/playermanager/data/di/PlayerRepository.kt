package com.dicosoluciones.changeplayer.playermanager.data.di

import com.dicosoluciones.changeplayer.playermanager.data.PlayerDao
import com.dicosoluciones.changeplayer.playermanager.data.PlayerEntity
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) {

    val players: Flow<List<PlayerModel>> =
        playerDao.getPlayers().map { items -> items.map { PlayerModel(it.id, it.name, it.stars) } }

    suspend fun add(playerModel: PlayerModel) {
        playerDao.addPlayer(playerModel.toData())
    }

    suspend fun update(playerModel: PlayerModel) {
        playerDao.updatePlayer(playerModel.toData())
    }

    suspend fun delete(playerModel: PlayerModel) {
        playerDao.deletePlayer(playerModel.toData())
    }

}

fun PlayerModel.toData():PlayerEntity {
    return PlayerEntity(this.id, this.name, this.stars)
}