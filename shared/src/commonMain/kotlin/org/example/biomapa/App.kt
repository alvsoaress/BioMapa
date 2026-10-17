@file:Suppress("SpellCheckingInspection")

package org.example.biomapa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
            // O botão de tema vai no cabeçalho de cada tela, para não cobrir conteúdo.
            val themeToggle: @Composable () -> Unit = {
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
                )
            }

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
                            actions = themeToggle,
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
                            actions = themeToggle,
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
                            actions = themeToggle,
                        )
                    }

                    composable<DetalhePlantaRoute>(
                        deepLinks = listOf(navDeepLink<DetalhePlantaRoute>(basePath = "biomapa://planta")),
                    ) { backStackEntry ->
                        val route = backStackEntry.toRoute<DetalhePlantaRoute>()
                        DetalhePlantaScreen(
                            plantaId = route.plantaId,
                            onBack = { navController.popBackStack() },
                            actions = themeToggle,
                        )
                    }
                }
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
            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto to "Alternar tema. Atual: automático, do sistema"
            ThemeMode.LIGHT -> Icons.Default.LightMode to "Alternar tema. Atual: claro"
            ThemeMode.DARK -> Icons.Default.DarkMode to "Alternar tema. Atual: escuro"
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
    actions: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var plantas by remember { mutableStateOf<List<Planta>>(emptyList()) }

    LaunchedEffect(Unit) {
        plantas =
            runCatching {
                supabase.postgrest["plantas"].select(columns = Columns.list("id", "user_id", "nome", "localizacao", "latitude", "longitude", "propriedades_medicas", "foto_url")).decodeList<Planta>()
            }.getOrElse { emptyList() }
    }

    val onLogout: () -> Unit = {
        coroutineScope.launch {
            if (!DEV_AUTH_BYPASS) {
                supabase.auth.signOut()
            }
            onLogoutClick()
        }
    }

    // O layout depende da largura da janela, não da orientação nem do tipo de aparelho.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentModifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp)
        if (maxWidth < LarguraMinimaMedia) {
            Column(
                modifier = contentModifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeHeader(actions = actions)
                Spacer(modifier = Modifier.height(16.dp))
                HomeIntro(textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(28.dp))
                PlantMapCard(
                    plantas = plantas,
                    onPlantClick = onPlantClick,
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                )
                Spacer(modifier = Modifier.height(28.dp))
                HomeButtons(onCreatePlantClick = onCreatePlantClick, onLogoutClick = onLogout)
            }
        } else {
            Row(
                modifier = contentModifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                PlantMapCard(
                    plantas = plantas,
                    onPlantClick = onPlantClick,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item { HomeHeader(actions = actions) }
                    item { HomeIntro(textAlign = TextAlign.Start) }
                    item { HomeButtons(onCreatePlantClick = onCreatePlantClick, onLogoutClick = onLogout) }
                    item {
                        Text(
                            text = "Plantas mapeadas",
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (plantas.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhuma planta cadastrada ainda.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    items(plantas, key = { it.id ?: it.hashCode() }) { planta ->
                        PlantListItem(planta = planta, onPlantClick = onPlantClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(actions: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Olá, Explorador! \uD83D\uDC4B",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        actions()
    }
}

@Composable
private fun HomeIntro(textAlign: TextAlign) {
    Text(
        text = "O BioMapa é feito pela nossa comunidade. Cadastre as plantas que você encontrar no campus, descreva suas propriedades medicinais e ajude a mapear a nossa flora!",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun HomeButtons(
    onCreatePlantClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onCreatePlantClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Registrar Nova Planta",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Sair da conta",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PlantListItem(
    planta: Planta,
    onPlantClick: (Int) -> Unit,
) {
    ListItem(
        headlineContent = { Text(planta.nome, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(planta.localizacao, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        modifier =
            Modifier.clickable(
                enabled = planta.id != null,
                onClickLabel = "Ver detalhes",
                role = Role.Button,
            ) { planta.id?.let(onPlantClick) },
    )
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
