package com.example.conecta_4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface JugadorDao {
    @Insert
    suspend fun inserta(jugador: Jugador)

    @Query("SELECT * FROM Jugador")
    suspend fun obtenerTodos(): List<Jugador>

    @Update
    suspend fun actualizar(jugador: Jugador)

    @Delete
    suspend fun eliminar(jugador: Jugador)

    @Query("SELECT * FROM Jugador WHERE nombre = :nombre COLLATE NOCASE LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): Jugador?

    @Query("SELECT * FROM Jugador WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Jugador?


}