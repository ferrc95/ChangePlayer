package com.dicosoluciones.changeplayer.playermanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * from PlayerEntity")
    fun getPlayers(): Flow<List<PlayerEntity>>

    @Insert
    suspend fun addPlayer(item:PlayerEntity)

    @Update
    suspend fun updatePlayer(item: PlayerEntity)

    @Delete
    suspend fun deletePlayer(item: PlayerEntity)

}