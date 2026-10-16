package br.ufrn.biomapa.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.clickable
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.ufrn.biomapa.domain.Planta

@Composable
fun TelaMapa(
    plantas: List<Planta>,
    onCadastrarClick: () -> Unit,
    onPlantaClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BioMapa — Mapa da UFRN") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCadastrarClick) {
                Icon(
		    imageVector = Icons.Default.Add,
                    contentDescription = "Cadastrar nova planta"
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Placeholder do mapa (substituir por mapa real depois)
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (plantas.isEmpty()) {
                    Text("Nenhuma planta cadastrada ainda. Toque no + para adicionar.")
                } else {
                    Text("Mapa da UFRN — ${plantas.size} plantas cadastradas")
                }
            }
            
            // Lista de plantas abaixo (provisório)
            LazyColumn {
                items(plantas, key = { it.id }) { planta ->
                    ListItem(
                        headlineContent = { Text(planta.nome) },
                        supportingContent = { Text(planta.descricao) },
                        modifier = Modifier.clickable { onPlantaClick(planta.id) }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun TelaMapaPreview() {
    MaterialTheme {
        TelaMapa(
            plantas = listOf(
                Planta("1", "Samambaia", "Sombra", "Calmante", null, -5.84, -35.20, "u1"),
                Planta("2", "Cacto", "Sol", "Diurético", null, -5.85, -35.21, "u1")
            ),
            onCadastrarClick = {},
            onPlantaClick = {}
        )
    }
}

@Preview
@Composable
fun TelaMapaVaziaPreview() {
    MaterialTheme {
        TelaMapa(plantas = emptyList(), onCadastrarClick = {}, onPlantaClick = {})
    }
}
