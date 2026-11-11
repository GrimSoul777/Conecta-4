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

    fun obtenerOCrearJugadores(
        nombre1: String,
        nombre2: String,
        alCompletar: (Jugador, Jugador) -> Unit
    ) {
        viewModelScope.launch {
            val jugador1 = obtenerOCrear(nombre1)
            val jugador2 = obtenerOCrear(nombre2)

            cargarJugadores()
            alCompletar(jugador1, jugador2)
        }
    }

    private suspend fun obtenerOCrear(nombre: String): Jugador {
        val nombreLimpio = nombre.trim()

        val jugadorExistente = jugadorDao.buscarPorNombre(nombreLimpio)
        if (jugadorExistente != null) {
            return jugadorExistente
        }

        val jugadorNuevo = Jugador(
            nombre = nombreLimpio,
            victorias = 0,
            derrotas = 0,
            empates = 0,
            partidas = 0
        )

        jugadorDao.inserta(jugadorNuevo)

        return jugadorDao.buscarPorNombre(nombreLimpio)
            ?: error("No se pudo encontrar el jugador recién guardado")
    }

    fun registrarPartida(idJugador1: Int, idJugador2: Int, ganadorId: Int?) {
        viewModelScope.launch {
            val jugador1 = jugadorDao.buscarPorId(idJugador1) ?: return@launch
            val jugador2 = jugadorDao.buscarPorId(idJugador2) ?: return@launch

            val actualizado1 = jugador1.copy(
                partidas = jugador1.partidas + 1,
                victorias = jugador1.victorias + if (ganadorId == jugador1.id) 1 else 0,
                derrotas = jugador1.derrotas + if (ganadorId == jugador2.id) 1 else 0,
                empates = jugador1.empates + if (ganadorId == null) 1 else 0
            )

            val actualizado2 = jugador2.copy(
                partidas = jugador2.partidas + 1,
                victorias = jugador2.victorias + if (ganadorId == jugador2.id) 1 else 0,
                derrotas = jugador2.derrotas + if (ganadorId == jugador1.id) 1 else 0,
                empates = jugador2.empates + if (ganadorId == null) 1 else 0
            )

            jugadorDao.actualizar(actualizado1)
            jugadorDao.actualizar(actualizado2)
            listaJugadores.value = jugadorDao.obtenerTodos()
        }
    }

    init {
        cargarJugadores()
    }
}