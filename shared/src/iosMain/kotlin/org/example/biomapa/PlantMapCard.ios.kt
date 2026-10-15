package org.example.biomapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun PlantMapCard(
    plantas: List<Planta>,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier,
) {
    IosFallbackPlantMapCard(plantas = plantas, modifier = modifier)
}

@Composable
private fun IosFallbackPlantMapCard(
    plantas: List<Planta>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Mapa Mapbox disponível no Android")
            Text("${plantas.size} plantas cadastradas")
        }
    }
}
