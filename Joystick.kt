package gamescreen

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class) // eksperymentalne funkcje Compose
@Composable
fun Joystick(onMove: suspend (dx: Float, dy: Float) -> Unit) { // Funkcja rysująca joystick, która przekazuje zmiany pozycji
    var joystickOffset by remember { mutableStateOf(Offset.Zero) } // Zmienna przechowująca aktualne przesunięcie joysticka
    val coroutineScope = rememberCoroutineScope() // Tworzy zakres przesuniecia

    Box( // Box do rozmieszczenia joysticka
        modifier = Modifier
            .padding(32.dp) // odstęp 32 dp
            .size(150.dp) // rozmiar joysticka na 150 dp
            .background(Color(0xAA000000), CircleShape) // tło joysticka na ciemne z zaokrąglonymi rogami
            .pointerInteropFilter { motionEvent -> // Obsługuje zdarzenia dotykowe
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN, // Obsługuje akcje dotknięcia ekranu
                    MotionEvent.ACTION_MOVE -> {
                        joystickOffset = Offset(
                            x = motionEvent.x - 75f, // Oblicza nowe położenie joysticka w osi X
                            y = motionEvent.y - 75f  // Oblicza nowe położenie joysticka w osi Y
                        )
                        coroutineScope.launch { // Uruchamia przesuniecie dla ruchu
                            onMove(joystickOffset.x, joystickOffset.y) // Wywołuje funkcję onMove z nowymi wartościami
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        joystickOffset = Offset.Zero // Resetuje położenie joysticka po zwolnieniu przycisniecia
                    }
                }
                true
            }
    )
}
