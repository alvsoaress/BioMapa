package org.example.biomapa.presentation

import org.example.biomapa.domain.model.Planta

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlantMapCard(
    plantas: List<Planta>,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
)
