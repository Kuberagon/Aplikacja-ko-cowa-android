package com.example.giera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedCharacter by remember { mutableStateOf("Zwiadowca") } // Stan przechowujący wybraną postać

    Column(
        modifier = Modifier
            .fillMaxSize() // Kolumna wypełnia cały ekran
            .background(Color(0xFFFAF3E0)) // Ustawia jasnobeżowe tło
            .padding(16.dp), // Dodaje wewnętrzny odstęp 16dp
        horizontalAlignment = Alignment.CenterHorizontally, // Ustawia wyrównanie elementów w poziomie
        verticalArrangement = Arrangement.SpaceBetween // Rozkłada elementy równomiernie w pionie
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Dodaje pustą przestrzeń na górze

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.game_logo), // Wczytuje logo gry z zasobów
                contentDescription = "Logo gry", // Opis logo dla dostępności
                modifier = Modifier
                    .size(200.dp) // Ustawia rozmiar logo na 200dp
                    .padding(bottom = 16.dp) // Dodaje odstęp poniżej logo
            )

            Text(
                text = "EvoLUTE2D", // Tekst tytułu gry
                fontSize = 36.sp, // Ustawia rozmiar tekstu
                color = Color.Black // Kolor czarny dla tekstu
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { selectedCharacter = "Zwiadowca" }, // Ustawia wybraną postać na "Zwiadowca"
                    modifier = Modifier
                        .weight(1f) // Każdy przycisk zajmuje równą szerokość
                        .padding(4.dp), // Dodaje odstęp wokół przycisku
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCharacter == "Zwiadowca") Color.Green else Color.Gray // Zmienia kolor przycisku w zależności od wyboru
                    )
                ) {
                    Text(text = "Zwiadowca", color = Color.White) // Tekst przycisku "Zwiadowca"
                }

                Button(
                    onClick = { selectedCharacter = "Bialy rycerz" }, // Ustawia wybraną postać na "Bialy rycerz"
                    modifier = Modifier
                        .weight(1f) // Każdy przycisk zajmuje równą szerokość
                        .padding(4.dp), // Dodaje odstęp wokół przycisku
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCharacter == "Bialy rycerz") Color.Green else Color.Gray // Zmienia kolor przycisku w zależności od wyboru
                    )
                ) {
                    Text(text = "Bialy rycerz", color = Color.White) // Tekst przycisku "Bialy rycerz"
                }
            }

            Button(
                onClick = { navController.navigate("game/$selectedCharacter") }, // Nawigacja do ekranu gry z przekazaniem wybranej postaci
                modifier = Modifier
                    .fillMaxWidth() // Przycisk zajmuje całą szerokość
                    .padding(vertical = 8.dp) // Dodaje odstęp powyżej i poniżej przycisku
                    .height(60.dp), // Ustawia wysokość przycisku
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006064)) // Ustawia kolor przycisku
            ) {
                Text(text = "Graj", fontSize = 20.sp, color = Color.White) // Tekst przycisku "Graj"
            }

            Button(
                onClick = { navController.navigate("login") }, // Nawigacja do ekranu logowania
                modifier = Modifier
                    .fillMaxWidth() // Przycisk zajmuje całą szerokość
                    .padding(vertical = 8.dp) // Dodaje odstęp powyżej i poniżej przycisku
                    .height(60.dp), // Ustawia wysokość przycisku
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Ustawia kolor przycisku
            ) {
                Text(text = "Wyloguj się", fontSize = 20.sp, color = Color.White) // Tekst przycisku "Wyloguj się"
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Dodaje pustą przestrzeń na dole
    }
}