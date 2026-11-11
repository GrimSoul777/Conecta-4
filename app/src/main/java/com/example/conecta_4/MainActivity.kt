package com.example.conecta_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                viewModel = viewModel,
                onNavigateJuego = {
                    navController.navigate("juego")
                },
                onNavigateScore = {
                    navController.navigate("score")
                }
            )
        }

        // Pantalla nuevo alumno
        composable("juego") {
            PantallaJuego(
                viewModel = viewModel,
                onHome = {
                    navController.navigate("inicio")
                }
            )
        }

        // Pantalla eliminar aluno
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
    onNavigateJuego: () -> Unit,
    onNavigateScore: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }

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
            value = nombre,
            onValueChange = {nombre = it}
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Jugador 2",
            fontSize =28.sp
        )
        TextField(
            value = nombre,
            onValueChange = {nombre = it}
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNavigateJuego
        ) {
            Text("Iniciar")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = onNavigateScore
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
}

@Composable
fun PantallaJuego(
    viewModel: JugadorViewModel,
    onHome: () -> Unit
) {

}