package com.example.conecta_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conecta_4.ui.theme.Conecta_4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Conecta_4Theme {
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

}

@Composable
fun PantallaScore(
    viewModel: JugadorViewModel,
    onHome: () -> Unit
){

}

@Composable
fun PantallaJuego(
    viewModel: JugadorViewModel,
    onHome: () -> Unit
) {

}