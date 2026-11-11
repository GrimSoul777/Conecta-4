package com.example.conecta_4

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Jugador::class], version = 1)
abstract class JugadorBaseDeDatos : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
}