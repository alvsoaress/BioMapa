@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import supabase

@Composable
fun App() {
    MaterialTheme {
        var isLoggedIn by remember { mutableStateOf(supabase.auth.currentSessionOrNull() != null) }
        var currentScreen by remember { mutableStateOf("home") }

        if (!isLoggedIn) {
            AuthScreen(
                onAuthSuccess = {
                    isLoggedIn = true
                    currentScreen = "home"
                }
            )
        } else {
            when (currentScreen) {
                "create_plant" -> {
                    CreatePlantScreen(
                        onPlantCreated = { currentScreen = "home" },
                        onBack = { currentScreen = "home" }
                    )
                }
                "home" -> {
                    HomeScreen(
                        onCreatePlantClick = { currentScreen = "create_plant" },
                        onLogoutClick = { isLoggedIn = false }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onCreatePlantClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de boas-vindas
        Text(
            text = "Olá, Explorador! \uD83D\uDC4B",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensagem convidativa
        Text(
            text = "O BioMapa é feito pela nossa comunidade. Cadastre as plantas que você encontrar no campus, descreva suas propriedades medicinais e ajude a mapear a nossa flora!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botão de Ação Principal
        Button(
            onClick = onCreatePlantClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Nova Planta", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Secundário
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    supabase.auth.signOut()
                    onLogoutClick()
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Sair")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da conta", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(onCreatePlantClick = {}, onLogoutClick = {})
    }
}