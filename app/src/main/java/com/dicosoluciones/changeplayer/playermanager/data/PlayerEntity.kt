package com.dicosoluciones.changeplayer.playermanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayerEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var stars: Int
)