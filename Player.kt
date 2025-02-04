package gamescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Player(tileSize: Dp, playerX: Int, playerY: Int, character: Int) {
    // Debugowanie
    println("Postać w Player: $character -> Drawable ID: $character")

    // Wyświetlenie gracza na planszy
    Box(
        modifier = Modifier
            .offset(
                x = (playerX * tileSize.value).dp,
                y = (playerY * tileSize.value).dp
            )
            .size(tileSize)
    ) {
        Image(
            painter = painterResource(id = character),
            contentDescription = "Gracz"
        )
    }
}
