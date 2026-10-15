@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import supabase

internal const val DEV_AUTH_BYPASS = true
internal const val DEV_USER_ID = "00000000-0000-0000-0000-000000000000"

@Serializable
object AuthRoute

@Serializable
object HomeRoute

@Serializable
object CreatePlantRoute

@Serializable
data class DetalhePlantaRoute(val plantaId: Int)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

@Composable
fun App() {
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    val isDarkTheme =
        when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

    BioMapaTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                var isLoggedIn by remember { mutableStateOf(DEV_AUTH_BYPASS || supabase.auth.currentSessionOrNull() != null) }

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) HomeRoute else AuthRoute,
                ) {
                    composable<AuthRoute> {
                        AuthScreen(
                            onAuthSuccess = {
                                isLoggedIn = true

                                navController.navigate(HomeRoute) {
                                    popUpTo(AuthRoute) { inclusive = true }
                                }
                            },
                        )
                    }

                    composable<HomeRoute> {
                        HomeScreen(
                            onCreatePlantClick = {
                                navController.navigate(CreatePlantRoute)
                            },
                            onPlantClick = { plantaId ->
                                navController.navigate(DetalhePlantaRoute(plantaId))
                            },
                            onLogoutClick = {
                                isLoggedIn = false

                                navController.navigate(AuthRoute) {
                                    popUpTo(HomeRoute) { inclusive = true }
                                }
                            },
                        )
                    }

                    composable<CreatePlantRoute> {
                        CreatePlantScreen(
                            onPlantCreated = {
                                navController.popBackStack()
                            },
                            onBack = {
                                navController.popBackStack()
                            },
                        )
                    }

                    composable<DetalhePlantaRoute>(
                        deepLinks = listOf(navDeepLink<DetalhePlantaRoute>(basePath = "biomapa://planta")),
                    ) { backStackEntry ->
                        val route = backStackEntry.toRoute<DetalhePlantaRoute>()
                        DetalhePlantaScreen(
                            plantaId = route.plantaId,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }

                ThemeToggleButton(
                    themeMode = themeMode,
                    onToggle = {
                        themeMode =
                            when (themeMode) {
                                ThemeMode.SYSTEM -> ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.DARK
                                ThemeMode.DARK -> ThemeMode.SYSTEM
                            }
                    },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ThemeToggleButton(
    themeMode: ThemeMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, description) =
        when (themeMode) {
            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto to "Tema: Automático (do sistema)"
            ThemeMode.LIGHT -> Icons.Default.LightMode to "Tema: Claro"
            ThemeMode.DARK -> Icons.Default.DarkMode to "Tema: Escuro"
        }

    FilledTonalIconButton(
        onClick = onToggle,
        modifier = modifier,
    ) {
        Icon(imageVector = icon, contentDescription = description)
    }
}

@Composable
fun HomeScreen(
    onCreatePlantClick: () -> Unit,
    onPlantClick: (Int) -> Unit,
    onLogoutClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var plantas by remember { mutableStateOf<List<Planta>>(emptyList()) }

    LaunchedEffect(Unit) {
        plantas =
            runCatching {
                supabase.postgrest["plantas"].select(columns = Columns.list("id", "user_id", "nome", "localizacao", "latitude", "longitude", "propriedades_medicas", "foto_url")).decodeList<Planta>()
            }.getOrElse { emptyList() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Olá, Explorador! \uD83D\uDC4B",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "O BioMapa é feito pela nossa comunidade. Cadastre as plantas que você encontrar no campus, descreva suas propriedades medicinais e ajude a mapear a nossa flora!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(28.dp))

        PlantMapCard(
            plantas = plantas,
            onPlantClick = onPlantClick,
            modifier = Modifier.fillMaxWidth().height(280.dp),
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onCreatePlantClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Nova Planta", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    if (!DEV_AUTH_BYPASS) {
                        supabase.auth.signOut()
                    }
                    onLogoutClick()
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sair da conta", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    BioMapaTheme {
        Surface {
            HomeScreen(
                onCreatePlantClick = {},
                onPlantClick = {},
                onLogoutClick = {},
            )
        }
    }
}
