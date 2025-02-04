package gamescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import com.example.giera.R
import kotlin.math.abs
import kotlin.random.Random

// Dane o potworze pozycja
data class Monster(
    val x: Int,
    val y: Int,
    val imageResource: Int
)

// Ekran gry
@Composable
fun GameScreen(navController: NavHostController, character: String) {
    val tileSize = 32.dp  // Rozmiar pojedynczego kafelka na mapie
    val mapSize = 10  // Rozmiar mapy (10x10)

    var playerX by remember { mutableIntStateOf(5) }  // Początkowa pozycja gracza w osi X
    var playerY by remember { mutableIntStateOf(5) }  // Początkowa pozycja gracza w osi Y

    // Lista potworów generowanych na mapie
    val monsters by remember {
        mutableStateOf(generateMonsters(mapSize))
    }

    // Funkcja ruchu gracza
    fun movePlayer(newX: Int, newY: Int) {
        // Sprawdzamy, czy nowa pozycja mieści się w granicach mapy
        if (newX in 0 until mapSize && newY in 0 until mapSize) {
            playerX = newX  // Aktualizujemy pozycję gracza w osi X
            playerY = newY  // Aktualizujemy pozycję gracza w osi Y

            // Sprawdzamy kolizję gracza z potworami po każdej zmianie pozycji
            monsters.forEach { monster ->
                // Jeśli gracz znajduje się w odległości <= 2 od potwora, przechodzi do ekranu walki
                if (abs(monster.x - playerX) <= 2f && abs(monster.y - playerY) <= 2f) {
                    navController.navigate("combat/${monster.imageResource}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Tło mapy gry
        Image(
            painter = painterResource(id = R.drawable.map_grass),
            contentDescription = "Mapa gry",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Wyświetlamy każdego potwora na mapie
        monsters.forEach { monster ->
            MonsterSprite(
                tileSize = tileSize * 1.6f,  // Rozmiar potwora powiększony o 60%
                monster = monster
            )
        }

        // Wyświetlamy gracza na mapie, w zależności od wybranego charakteru
        Player(
            tileSize = tileSize * 2f,  // Powiększenie rozmiaru gracza
            playerX = playerX,
            playerY = playerY,
            character = when (character) {
                "Zwiadowca" -> R.drawable.gracz_1  // Obrazek Zwiadowcy
                "Biały Rycerz" -> R.drawable.gracz_2  // Obrazek Białego Rycerza
                else -> R.drawable.gracz_1  // Domyślny obrazek
            }
        )

        // Przycisk ruchu w dół, góra, lewo, prawo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Ruch w górę
                Button(
                    onClick = { movePlayer(playerX, playerY - 1) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("GÓRA", fontSize = 16.sp)
                }

                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    // Ruch w lewo
                    Button(
                        onClick = { movePlayer(playerX - 1, playerY) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("LEWO", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(50.dp))

                    // Ruch w prawo
                    Button(
                        onClick = { movePlayer(playerX + 1, playerY) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("PRAWO", fontSize = 16.sp)
                    }
                }

                // Ruch w dół
                Button(
                    onClick = { movePlayer(playerX, playerY + 1) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("DÓŁ", fontSize = 16.sp)
                }
            }

            // Przycisk do ekwipunku w prawym górnym rogu
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Button(
                    onClick = { navController.navigate("inventory") },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(120.dp, 50.dp)
                ) {
                    Text("Ekwipunek", fontSize = 16.sp)
                }
            }
        }
    }
}

// KOMPOSABLE reprezentujące potwora
@Composable
fun MonsterSprite(tileSize: Dp, monster: Monster) {
    Box(
        modifier = Modifier
            .offset(
                x = (monster.x * tileSize.value).dp,  // Pozycja na osi X
                y = (monster.y * tileSize.value).dp   // Pozycja na osi Y
            )
            .size(tileSize)  // Rozmiar sprite'a potwora
    ) {
        Image(
            painter = painterResource(id = monster.imageResource),
            contentDescription = "Potwór"
        )
    }
}

// Funkcja generująca listę potworów na mapie
private fun generateMonsters(mapSize: Int): List<Monster> {
    val monsterImages = listOf(R.drawable.monster1, R.drawable.monster2)  // Lista obrazków potworów
    val monsters = mutableListOf<Monster>()
    val startingPlayerX = 5
    val startingPlayerY = 5

    // Tworzymy 3 potwory na mapie
    while (monsters.size < 3) {
        val x = Random.nextInt(mapSize)  // Losowa pozycja na osi X
        val y = Random.nextInt(mapSize)  // Losowa pozycja na osi Y

        // Sprawdzamy, czy potwór nie jest za blisko gracza na starcie
        if (abs(x - startingPlayerX) > 1 || abs(y - startingPlayerY) > 1) {
            // Dodajemy potwora, jeśli jest wystarczająco daleko od gracza
            monsters.add(
                Monster(
                    x = x,
                    y = y,
                    imageResource = monsterImages[Random.nextInt(monsterImages.size)]  // Losowy obrazek potwora
                )
            )
        }
    }

    return monsters  // Zwracamy listę potworów
}
