package gamescreen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawMap( // Funkcja rysująca mapę
    tileSizePx: Float, // Rozmiar kafelka w pikselach
    mapWidth: Int, // Szerokość mapy
    mapHeight: Int, // Wysokość mapy
    trees: List<Pair<Int, Int>> // Lista współrzędnych drzew
) {
    for (x in 0 until mapWidth) { // Iteracja po szerokości mapy
        for (y in 0 until mapHeight) { // Iteracja po wysokości mapy
            drawRect( // Rysuje prostokąt
                color = if (trees.contains(Pair(x, y))) Color(0xFF228B22) else Color(0xFF7CFC00), // Kolor zależny od obecności drzewa
                topLeft = Offset(x * tileSizePx, y * tileSizePx), // Oblicza położenie kafelka
                size = Size(tileSizePx, tileSizePx) // Ustawia rozmiar kafelka
            )
        }
    }
}
