package com.example.giera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.giera.ui.theme.GieraTheme
import gamescreen.CombatScreen
import gamescreen.GameScreen
import gamescreen.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GieraTheme {
                val navController = rememberNavController()  // Tworzenie kontrolera nawigacji

                // Konfiguracja nawigacji w aplikacji -> przejscia miedzy ekranami
                NavHost(navController = navController, startDestination = "login") {
                    // Ekran logowania
                    composable("login") { LoginScreen(navController) }
                    // Ekran główny
                    composable("home") { HomeScreen(navController) }
                    // Ekran gry
                    composable(
                        route = "game/{character}"
                    ) { backStackEntry ->
                        val character = backStackEntry.arguments?.getString("character") ?: "Zwiadowca" // pobranie postaci
                        GameScreen(navController = navController, character = character)  // Ekran gry
                    }
                    // Ekran walki
                    composable(
                        route = "combat/{monsterImage}"
                    ) { backStackEntry ->
                        val monsterImage = backStackEntry.arguments?.getString("monsterImage")?.toIntOrNull()
                            ?: R.drawable.monster1 //pobranie postaci
                        CombatScreen(navController = navController, monsterImage = monsterImage)  // Ekran walki
                    }
                    // Ekran ekwipunku
                    composable("inventory") { InventoryScreen(navController) }
                    // Ekran porażki
                    composable("game/defeat") {
                        DefeatScreen(navController)
                    }
                }
            }
        }
    }

    // Zapis stanu gry przed wstrzymaniem aplikacji
    override fun onPause() {
        super.onPause()
        scope.launch {
            FirebaseManager.saveScore(GameState.gold)  // Zapis stanu gry (złoto)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}

@Composable
fun DefeatScreen(navController: androidx.navigation.NavHostController) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,  // Wyśrodkowanie w poziomie
        verticalArrangement = Arrangement.Center  // Wyśrodkowanie w pionie
    ) {
        // Wyświetlanie czaszki przy porażce
        Image(
            painter = painterResource(id = R.drawable.skull),
            contentDescription = "Porażka",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Przegrałeś!",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Przycisk, który umożliwia powrót do menu głównego
        Button(
            onClick = {
                scope.launch {
                    // Zapisanie wyniku przed resetem
                    FirebaseManager.saveHighScore(GameState.gold)
                    GameState.resetState()  // Resetowanie stanu gry
                }
                navController.navigate("home") {  // Nawigacja do ekranu głównego
                    popUpTo("login") { inclusive = false }
                }
            }
        ) {
            Text(text = "Wróć do menu głównego") 
        }
    }
}
