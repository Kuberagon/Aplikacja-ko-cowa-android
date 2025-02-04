package com.example.giera

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Inicjalizacja Firebase Authentication

    var email by remember { mutableStateOf("") } // deklaracja dla emaila czy użytkownik jest zalogowany
    var password by remember { mutableStateOf("") } // Stan dla hasła
    var isLogin by remember { mutableStateOf(true) } // true zalogowany false niezalogowany

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0)) // Jasnobeżowe tło
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_logo),
            contentDescription = "Logo gry",
            modifier = Modifier.size(300.dp).padding(bottom = 16.dp) //ustawienie Logo gry
        )

        Text(
            text = if (isLogin) "Logowanie" else "Rejestracja",
            fontSize = 28.sp,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // Aktualizacja stanu emaila
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color(0xFFFFD700),
                unfocusedLabelColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it }, // Aktualizacja stanu hasła
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color(0xFFFFD700),
                unfocusedLabelColor = Color.Gray
            )
        )

        Button(
            onClick = {
                if (isLogin) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Zalogowano!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") // Nawigacja do ekranu głównego
                            } else {
                                Toast.makeText(context, "Błąd logowania", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Zarejestrowano!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Błąd rejestracji", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006064))
        ) {
            Text(text = if (isLogin) "Zaloguj" else "Zarejestruj", fontSize = 18.sp)
        }

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                text = if (isLogin) "Nie masz konta? Zarejestruj się" else "Masz konto? Zaloguj się",
                color = Color.Black
            )
        }
    }
}
