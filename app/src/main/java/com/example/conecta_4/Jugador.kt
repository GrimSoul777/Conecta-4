package com.example.conecta_4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Jugador (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val victorias: Int,
    val derrotas: Int,
    val empates: Int,
    val partidas: Int
)