package org.example.biomapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.jetbrains.compose.ui.tooling.preview.Preview
import supabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhePlantaScreen(
    plantaId: Int,
    onBack: () -> Unit,
    actions: @Composable () -> Unit = {},
) {
    var planta by remember { mutableStateOf<Planta?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(plantaId) {
        planta =
            runCatching {
                supabase.postgrest["plantas"]
                    .select(
                        columns =
                            Columns.list(
                                "id",
                                "user_id",
                                "nome",
                                "localizacao",
                                "latitude",
                                "longitude",
                                "propriedades_medicas",
                                "foto_url",
                            ),
                    )
                    .decodeList<Planta>()
                    .firstOrNull { it.id == plantaId }
            }.getOrNull()
        carregando = false
    }

    DetalhePlantaContent(planta = planta, carregando = carregando, onBack = onBack, actions = actions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhePlantaContent(
    planta: Planta?,
    carregando: Boolean,
    onBack: () -> Unit,
    actions: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes da planta",
                        modifier = Modifier.semantics { heading() },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                        )
                    }
                },
                actions = { actions() },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                carregando -> CircularProgressIndicator()

                planta == null ->
                    Text(
                        text = "Planta não encontrada.",
                        style = MaterialTheme.typography.bodyLarge,
                    )

                else -> {
                    val p = planta
                    Column(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .widthIn(max = LarguraMaximaConteudo)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = p.nome,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Local: ${p.localizacao}",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Propriedades medicinais",
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = p.propriedadesMedicas,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DetalhePlantaScreenPreview() {
    BioMapaTheme {
        Surface {
            DetalhePlantaContent(
                planta =
                    Planta(
                        id = 1,
                        userId = DEV_USER_ID,
                        nome = "Boldo",
                        localizacao = "Próximo ao Setor de Aulas II",
                        propriedadesMedicas = "Auxilia na digestão e no alívio de desconfortos estomacais.",
                    ),
                carregando = false,
                onBack = {},
            )
        }
    }
}
