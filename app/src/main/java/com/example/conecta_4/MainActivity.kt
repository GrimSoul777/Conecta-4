package com.example.conecta_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.util.TableInfo
import com.example.conecta_4.ui.theme.Conecta_4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            JugadorBaseDeDatos::class.java,
            "jugadores_db"
        ).build()
        val jugadorDao = db.jugadorDao()

        setContent {
            Conecta_4Theme {
                AppNavigation(jugadorDao = jugadorDao)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(jugadorDao: JugadorDao) {
    val navController = rememberNavController()
    val viewModel = remember { JugadorViewModel(jugadorDao) }
    var jugadoresPartida by remember { mutableStateOf<Pair<Jugador, Jugador>?>(null) }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                viewModel = viewModel,
                onNavigateJuego = { jugador1, jugador2 ->
                    jugadoresPartida = jugador1 to jugador2
                    navController.navigate("juego")
                },
                onNavigateScore = {
                    navController.navigate("score")
                }
            )
        }

        composable("juego") {
            val jugadores = jugadoresPartida

            if (jugadores != null) {
                PantallaJuego(
                    jugador1 = jugadores.first,
                    jugador2 = jugadores.second,
                    viewModel = viewModel,
                    onHome = {
                        navController.navigate("inicio")
                    }
                )
            }
        }

        composable("score") {
            PantallaScore(
                viewModel = viewModel,
                onHome = {
                    navController.navigate("inicio")
                }
            )
        }
    }
}

@Composable
fun PantallaInicio(
    viewModel: JugadorViewModel,
    onNavigateJuego: (Jugador, Jugador) -> Unit,
    onNavigateScore: () -> Unit
) {
    var nombreJugador1 by remember { mutableStateOf("") }
    var nombreJugador2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Jugador 1",
            fontSize =28.sp
        )
        TextField(
            value = nombreJugador1,
            onValueChange = {nombreJugador1 = it},
            label = { Text("Nombre del jugador 1") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Jugador 2",
            fontSize =28.sp
        )
        TextField(
            value = nombreJugador2,
            onValueChange = {nombreJugador2 = it},
            label = { Text("Nombre del jugador 2") }
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (error.isNotEmpty()) {
            Text(error)
        }

        Button(
            onClick = {
                if (!toquePermitido()) return@Button

                val nombre1 = nombreJugador1.trim()
                val nombre2 = nombreJugador2.trim()

                when {
                    nombre1.isEmpty() || nombre2.isEmpty() ->
                        error = "Escribe el nombre de los dos jugadores."

                    nombre1.equals(nombre2, ignoreCase = true) ->
                        error = "Cada jugador debe tener un nombre diferente."

                    else -> {
                        error = ""
                        viewModel.obtenerOCrearJugadores(nombre1, nombre2) { jugador1, jugador2 ->
                            onNavigateJuego(jugador1, jugador2)
                        }
                    }
                }
            }
        ) {
            Text("Iniciar")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                if (!toquePermitido()) return@Button
                onNavigateScore}
        ) {
            Text("Score")
        }
    }
}

@Composable
fun PantallaScore(
    viewModel: JugadorViewModel,
    onHome: () -> Unit
){
    val listaJugadores = viewModel.listaJugadores.value
        .sortedByDescending { it.victorias }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            text = "SCORES",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (listaJugadores.isEmpty()) {
            Text("Todavía no hay jugadores guardados.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = listaJugadores,
                    key = { jugador -> jugador.id }
                ) { jugador ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = jugador.nombre,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Victorias: ${jugador.victorias}")
                            Text("Derrotas: ${jugador.derrotas}")
                            Text("Empates: ${jugador.empates}")
                            Text("Partidas: ${jugador.partidas}")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}

@Composable
fun PantallaJuego(
    jugador1: Jugador,
    jugador2: Jugador,
    viewModel: JugadorViewModel,
    onHome: () -> Unit
) {
    var tablero by remember { mutableStateOf(List(6) { List(6) { 0 } }) }
    var turno by remember { mutableStateOf(1) }
    var juegoIniciado by remember { mutableStateOf(false) }
    var partidaFinalizada by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf("") }

    val enCurso = juegoIniciado && !partidaFinalizada
    val jugadorActual = if (turno == 1) jugador1 else jugador2

    fun finalizarPartida(ganadorId: Int?, mensaje: String) {
        if (partidaFinalizada) return

        partidaFinalizada = true
        resultado = mensaje
        viewModel.registrarPartida(jugador1.id, jugador2.id, ganadorId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = when {
                !juegoIniciado -> "Presiona Iniciar para jugar"
                partidaFinalizada -> resultado
                else -> "Turno -> ${jugadorActual.nombre}"
            },
            fontSize = 20.sp
        )

        //Btones para elegir cada columna
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(6) { columna ->
                val columnaLlena = tablero[0][columna] != 0

                Button(
                    onClick = {
                        if (!toquePermitido()) return@Button

                        val fila = (5 downTo 0)
                            .firstOrNull { tablero[it][columna] == 0 }

                        if (fila != null && enCurso) {
                            val ficha = turno
                            val nuevoTablero = tablero.map { it.toMutableList() }
                            nuevoTablero[fila][columna] = ficha
                            tablero = nuevoTablero.map { it.toList() }

                            if (hayCuatroEnLinea(nuevoTablero, fila, columna, ficha)) {
                                val ganador = if (ficha == 1) jugador1 else jugador2
                                finalizarPartida(
                                    ganador.id,
                                    "¡Ganó ${ganador.nombre}!"
                                )
                            } else if (nuevoTablero.all { filaTablero ->
                                    filaTablero.all { casilla -> casilla != 0 }
                                }
                            ) {
                                finalizarPartida(null, "¡Empate!")
                            } else {
                                turno = if (turno == 1) 2 else 1
                            }
                        }
                    },
                    enabled = enCurso && !columnaLlena,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("↓")
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = false
        ) {
            items(36) { indice ->
                val fila = indice / 6
                val columna = indice % 6
                val ficha = tablero[fila][columna]

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(Color(0xFF808080), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (ficha != 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .background(
                                    color = if (ficha == 1) Color.Red else Color.Blue,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    if (!toquePermitido()) return@Button

                    tablero = List(6) { List(6) { 0 } }
                    turno = (1..2).random()
                    resultado = ""
                    partidaFinalizada = false
                    juegoIniciado = true
                },
                enabled = !enCurso,
                modifier = Modifier.weight(1f)
            ) {
                Text("Iniciar")
            }

            Button(
                onClick = {
                    if (!toquePermitido()) return@Button

                    val ganador = if (turno == 1) jugador2 else jugador1
                    finalizarPartida(
                        ganador.id,
                        "${jugadorActual.nombre} se rindió. ¡Ganó ${ganador.nombre}!"
                    )
                },
                enabled = enCurso,
                modifier = Modifier.weight(1f)
            ) {
                Text("Rendirse")
            }

            Button(
                onClick = {
                    if (!toquePermitido()) return@Button
                    onHome()
                },
                enabled = !enCurso,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cambiar jugador")
            }
        }
    }
}

private fun hayCuatroEnLinea(
    tablero: List<List<Int>>,
    fila: Int,
    columna: Int,
    ficha: Int
): Boolean {
    val direcciones = listOf(
        0 to 1,   // horizontal ->
        1 to 0,   // vertical <-
        1 to 1,   // diagonal /
        1 to -1   // diagonal inversa \
    )

    return direcciones.any { (deltaFila, deltaColumna) ->
        var total = 1

        for (sentido in listOf(-1, 1)) {
            var f = fila + deltaFila * sentido
            var c = columna + deltaColumna * sentido

            while (
                f in 0..5 && c in 0..5 && tablero[f][c] == ficha
            ) {
                total++
                f += deltaFila * sentido
                c += deltaColumna * sentido
            }
        }
        total >= 4
    }
}

//No click a botones al mismo tiempo
private fun toquePermitido(): Boolean {
    val ahora = android.os.SystemClock.elapsedRealtime()
    if (ahora - ultimoToque < 500L) return false

    ultimoToque = ahora
    return true
}
private var ultimoToque = 0L