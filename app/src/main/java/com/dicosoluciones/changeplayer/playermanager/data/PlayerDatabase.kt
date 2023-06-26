package com.dicosoluciones.changeplayer.playermanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlayerEntity::class], version = 1)
abstract class PlayerDatabase: RoomDatabase() {
    abstract fun playerDao():PlayerDao
}