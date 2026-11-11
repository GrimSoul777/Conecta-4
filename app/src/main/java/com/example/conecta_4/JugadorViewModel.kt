package com.example.conecta_4

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class JugadorViewModel(private val jugadorDao: JugadorDao) : ViewModel() {
    var listaJugadores = mutableStateOf<List<Jugador>>(emptyList())
        private set

    var mensaje = mutableStateOf("")
        private set

    fun cargarJugadores() {
        viewModelScope.launch {
            listaJugadores.value = jugadorDao.obtenerTodos()
        }
    }

    fun agregarJugador(jugador: Jugador) {
        viewModelScope.launch {
            jugadorDao.inserta(jugador)
            cargarJugadores()
            mensaje.value = "Jugador agregado correctamente"
        }
    }

    fun actualizarJugador(jugador: Jugador) {
        viewModelScope.launch {
            jugadorDao.actualizar(jugador)
            cargarJugadores()
            mensaje.value = "Jugador actualizado correctamente"
        }
    }

    fun eliminarJugador(jugador: Jugador) {
        viewModelScope.launch {
            jugadorDao.eliminar(jugador)
            cargarJugadores()
            mensaje.value = "Jugador eliminado correctamente"
        }
    }

    init {
        cargarJugadores()
    }
}