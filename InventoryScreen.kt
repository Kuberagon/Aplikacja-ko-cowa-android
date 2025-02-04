package com.example.giera

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import gamescreen.GameState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore("game_preferences")

@Composable
fun InventoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val inventoryBackground = painterResource(id = R.drawable.inventory_background)
    var currentGold by remember { mutableIntStateOf(GameState.gold) }

    // Ładowanie zapisanych danych z DataStore
    LaunchedEffect(Unit) {
        context.dataStore.data.collectLatest { preferences ->
            val savedGold = preferences[intPreferencesKey("gold")] ?: 0
            currentGold = savedGold
            GameState.gold = savedGold
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2D2D2D))
    ) {
        Image(
            painter = inventoryBackground,
            contentDescription = "Tło ekwipunku",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ekwipunek Gracza",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )

            // Karta wyświetlająca ilość złota
            Card(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3D3D3D).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Złoto",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentGold.toString(),
                        fontSize = 32.sp,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Karta ulepszeń statystyk
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3D3D3D).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Koszt ulepszenia: 5 złota",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Przycisk dla statystyki Siła
                    StatButton(
                        name = "Siła",
                        color = Color(0xFFFF4444),
                        value = GameState.strength,
                        onClick = {
                            if (GameState.gold >= 5) {
                                GameState.strength++
                                GameState.gold -= 5
                                currentGold = GameState.gold
                                scope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[intPreferencesKey("gold")] = GameState.gold
                                    }
                                    // Zapisujemy aktualny stan do Firebase
                                    FirebaseManager.saveScore(GameState.gold)
                                }
                            }
                        }
                    )

                    // Przycisk dla statystyki Intelekt
                    StatButton(
                        name = "Intelekt",
                        color = Color(0xFF44AAFF),
                        value = GameState.intelligence,
                        onClick = {
                            if (GameState.gold >= 5) {
                                GameState.intelligence++
                                GameState.gold -= 5
                                currentGold = GameState.gold
                                scope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[intPreferencesKey("gold")] = GameState.gold
                                    }
                                    // Zapisujemy aktualny stan do Firebase
                                    FirebaseManager.saveScore(GameState.gold)
                                }
                            }
                        }
                    )

                    // Przycisk dla statystyki Zręczność
                    StatButton(
                        name = "Zręczność",
                        color = Color(0xFF44FF44),
                        value = GameState.dexterity,
                        onClick = {
                            if (GameState.gold >= 5) {
                                GameState.dexterity++
                                GameState.gold -= 5
                                currentGold = GameState.gold
                                scope.launch {
                                    context.dataStore.edit { preferences ->
                                        preferences[intPreferencesKey("gold")] = GameState.gold
                                    }
                                    // Zapisujemy aktualny stan do Firebase
                                    FirebaseManager.saveScore(GameState.gold)
                                }
                            }
                        }
                    )
                }
            }

            // Przycisk "Zapisz i wyjdź"
            Button(
                onClick = {
                    scope.launch {
                        // Zapisujemy najlepszy wynik
                        FirebaseManager.saveHighScore(GameState.gold)
                        // Zapisujemy aktualny stan do historii
                        FirebaseManager.saveScore(GameState.gold)
                        // Resetujemy stan gry
                        GameState.resetState()
                        // Wracamy do ekranu głównego
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Zapisz i wyjdź",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Przycisk "Powrót do gry"
            Button(
                onClick = {
                    navController.navigate("game/${GameState.character}") {
                        popUpTo("inventory") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0044FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Powrót do gry",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatButton(
    name: String,
    color: Color,
    onClick: () -> Unit,
    value: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}