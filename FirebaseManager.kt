package com.example.giera

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import gamescreen.GameState
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveHighScore(gold: Int) {
        val currentUser = auth.currentUser
        if (currentUser?.email != null) {
            val scoreData = hashMapOf(
                "email" to currentUser.email,
                "gold" to gold,
                "date" to formatDate(System.currentTimeMillis()),
                // Dodajemy statystyki postaci
                "character" to GameState.character,
                "strength" to GameState.strength,
                "intelligence" to GameState.intelligence,
                "dexterity" to GameState.dexterity
            )

            try {
                val existingScore = db.collection("highscores")
                    .document(currentUser.email!!)
                    .get()
                    .await()

                if (!existingScore.exists() || (existingScore.getLong("gold") ?: 0) < gold) {
                    db.collection("highscores")
                        .document(currentUser.email!!)
                        .set(scoreData)
                        .await()
                }
            } catch (e: Exception) {
                println("Błąd zapisu highscore: ${e.message}")
            }
        }
    }

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
                db.collection("scores")
                    .document(currentUser.email!!)
                    .collection("games")
                    .add(gameData)
                    .await()

                println("Pomyślnie zapisano wynik gracza")
            } catch (e: Exception) {
                println("Błąd zapisu wyniku: ${e.message}")
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}