package gamescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.navigation.NavHostController
import com.example.giera.FirebaseManager
import com.example.giera.dataStore
import kotlinx.coroutines.launch
import kotlin.random.Random

// Dane walki, trzymające stan walki
data class CombatData(
    val playerHealth: Int = 20,
    val monsterHealth: Int = 20 + ((GameState.gold / 5) * 10), // +10 HP dla potwora co każde 20 złota
    val monsterImage: Int,
    val lastActionMessage: String = "",
    val turnsSinceLastMagic: Int = 0 // Liczba tur od ostatniego użycia uderzenia magii
)

// Ekran walki
@Composable
fun CombatScreen(navController: NavHostController, monsterImage: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var combatData by remember {
        mutableStateOf(CombatData(monsterImage = monsterImage))
    }

    // Obliczenia na obrażenia zadane łukiem
    fun getBowDamage(): Int {
        val chance = Random.nextFloat() * 100
        val baseDamage = when {
            chance < 20 -> 3 // 20% szans na 3 obrażenia
            chance < 60 -> 2 // 40% szans na 2 obrażenia
            else -> 1 // 40% szans na 1 obrażenie
        }
        return baseDamage + GameState.dexterity
    }

    // Obliczenia na obrażenia zadane przez potwora
    fun getMonsterDamage(): Int {
        val chance = Random.nextFloat() * 100
        return if (chance < 60) 1 else 2 // 60% na 1 obrażenie, 40% na 2 obrażenia
    }

    // Kody na przetworzenie tury potwora
    fun handleMonsterTurn(currentData: CombatData): CombatData {
        val damage = getMonsterDamage()
        return currentData.copy(
            playerHealth = currentData.playerHealth - damage,
            lastActionMessage = currentData.lastActionMessage + "\nPotwór zadał $damage obrażenia!",
            turnsSinceLastMagic = currentData.turnsSinceLastMagic + 1
        )
    }

    // Sprawdzenie warunku, czy gra się zakończyła (wygrana lub przegrana)
    fun checkGameEnd(data: CombatData) {
        if (data.monsterHealth <= 0) {
            GameState.gold += 1
            scope.launch {
                context.dataStore.edit { preferences ->
                    preferences[intPreferencesKey("gold")] = GameState.gold
                }
                FirebaseManager.saveScore(GameState.gold)
            }
            navController.popBackStack()
        } else if (data.playerHealth <= 0) {
            scope.launch {
                FirebaseManager.saveHighScore(GameState.gold)
            }
            navController.navigate("game/defeat") {
                popUpTo("game") { inclusive = false }
            }
        }
    }

    // UI walki
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Informacje o walce
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Walka z potworem!", fontSize = 24.sp)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Twoje życie", fontSize = 18.sp)
                        Text("${combatData.playerHealth}/20", fontSize = 24.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Życie potwora", fontSize = 18.sp)
                        Text("${combatData.monsterHealth}/${20 + ((GameState.gold / 20) * 10)}", fontSize = 24.sp)
                    }
                }

                Image(
                    painter = painterResource(id = combatData.monsterImage),
                    contentDescription = "Potwór",
                    modifier = Modifier.size(200.dp)
                )

                if (combatData.lastActionMessage.isNotEmpty()) {
                    Text(
                        text = combatData.lastActionMessage,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Przyciski akcji
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Atak mieczem
                Button(
                    onClick = {
                        val damage = 1 + GameState.strength
                        combatData = combatData.copy(
                            monsterHealth = combatData.monsterHealth - damage,
                            lastActionMessage = "Zadałeś $damage obrażenia mieczem!"
                        )
                        if (combatData.monsterHealth > 0) {
                            combatData = handleMonsterTurn(combatData)
                        }
                        checkGameEnd(combatData)
                    },
                    modifier = Modifier.width(200.dp).height(50.dp)
                ) {
                    Text("Atak mieczem (${1 + GameState.strength} ❤)", fontSize = 18.sp)
                }

                // Atak magiczny
                Button(
                    onClick = {
                        if (combatData.turnsSinceLastMagic >= 3) {
                            val damage = 2 + GameState.intelligence
                            combatData = combatData.copy(
                                monsterHealth = combatData.monsterHealth - damage,
                                lastActionMessage = "Zadałeś $damage obrażenia magią!",
                                turnsSinceLastMagic = 0
                            )
                            if (combatData.monsterHealth > 0) {
                                combatData = handleMonsterTurn(combatData)
                            }
                            checkGameEnd(combatData)
                        } else {
                            combatData = combatData.copy(
                                lastActionMessage = "Musisz poczekać jeszcze ${3 - combatData.turnsSinceLastMagic} tury aby użyć magii!"
                            )
                        }
                    },
                    enabled = combatData.turnsSinceLastMagic >= 3,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.width(200.dp).height(50.dp)
                ) {
                    Text("Atak magiczny (${2 + GameState.intelligence} ❤)", fontSize = 18.sp)
                }

                // Strzał z łuku
                Button(
                    onClick = {
                        val damage = getBowDamage()
                        combatData = combatData.copy(
                            monsterHealth = combatData.monsterHealth - damage,
                            lastActionMessage = "Zadałeś $damage obrażenia łukiem!"
                        )
                        if (combatData.monsterHealth > 0) {
                            combatData = handleMonsterTurn(combatData)
                        }
                        checkGameEnd(combatData)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.width(200.dp).height(50.dp)
                ) {
                    Text("Strzał z łuku (${1 + GameState.dexterity}-${3 + GameState.dexterity} ❤)", fontSize = 18.sp)
                }
            }
        }

        // Przycisk ucieczki
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("game") {
                        popUpTo("game") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.width(120.dp).height(40.dp)
            ) {
                Text("Uciekaj", fontSize = 16.sp)
            }
        }
    }
}
