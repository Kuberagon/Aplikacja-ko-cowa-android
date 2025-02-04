package com.example.giera

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import gamescreen.GameState
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseManager {
    // Inicjalizacja Firestore
    private val db = FirebaseFirestore.getInstance()
    // Inicjalizacja Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    // Funkcja zapisująca najwyższy wynik gracza
    suspend fun saveHighScore(gold: Int) {
        val currentUser = auth.currentUser
        // Sprawdzamy, czy użytkownik jest zalogowany
        if (currentUser?.email != null) {
            val scoreData = hashMapOf(
                "email" to currentUser.email,  // Zapisujemy email użytkownika
                "gold" to gold,  // liczbę zdobytych monet
                "date" to formatDate(System.currentTimeMillis()),  // Aktualną datę i godzinę
                "character" to GameState.character,  // Postać gracza
                "strength" to GameState.strength,  // Siłę
                "intelligence" to GameState.intelligence,  // Inteligencję
                "dexterity" to GameState.dexterity  // Zręczność
            )

            try {
                // Pobieramy istniejący wynik użytkownika
                val existingScore = db.collection("highscores")
                    .document(currentUser.email!!)
                    .get()
                    .await()

                // Aktualizujemy wynik tylko wtedy, gdy jest on wyższy niż poprzedni dla highscores
                if (!existingScore.exists() || (existingScore.getLong("gold") ?: 0) < gold) {
                    db.collection("highscores")
                        .document(currentUser.email!!)
                        .set(scoreData)
                        .await()
                }
            } catch (e: Exception) {
                println("Błąd zapisu highscore: ${e.message}")  // dla  błędu
            }
        }
    }

    // Funkcja zapisująca wynik każdej rozgrywki
    suspend fun saveScore(gold: Int) {
        val currentUser = auth.currentUser

        if (currentUser?.email != null) {
            val gameData = hashMapOf(
                "gold" to gold,
                "date" to formatDate(System.currentTimeMillis()),
                "character" to GameState.character,
                "strength" to GameState.strength,
                "intelligence" to GameState.intelligence,
                "dexterity" to GameState.dexterity
            )

            try {
                // Dodajemy wynik do scores
                db.collection("scores")
                    .document(currentUser.email!!)
                    .collection("games")
                    .add(gameData)
                    .await()

                println("Pomyślnie zapisano wynik gracza")  // poprawny zapisi
            } catch (e: Exception) {
                println("Błąd zapisu wyniku: ${e.message}")  // błędny zapis
            }
        }
    }

    // Funkcja przekształcająca datę z timeestamp na wartosci
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
