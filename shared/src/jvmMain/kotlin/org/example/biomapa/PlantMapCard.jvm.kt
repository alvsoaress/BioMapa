package org.example.biomapa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
actual fun PlantMapCard(
    plantas: List<Planta>,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier,
) {
    FallbackPlantMapCard(plantas = plantas, onPlantClick = onPlantClick, modifier = modifier)
}

@Composable
internal fun FallbackPlantMapCard(
    plantas: List<Planta>,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Mapa do campus",
) {
    val primary = MaterialTheme.colorScheme.primary
    val currentLocationColor = MaterialTheme.colorScheme.tertiary
    val plantMarkerColor = MaterialTheme.colorScheme.secondary
    val mapBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val pathColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Canvas(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                        .background(mapBackground)
                        .pointerInput(plantas) {
                            detectTapGestures { tap ->
                                val threshold = 40f
                                var bestId: Int? = null
                                var bestDistance = Float.MAX_VALUE

                                plantas.take(12).forEach { planta ->
                                    val id = planta.id ?: return@forEach
                                    val position = markerOffsetFor(planta, size.width.toFloat(), size.height.toFloat())
                                    val distance = (tap - position).getDistance()
                                    if (distance < threshold && distance < bestDistance) {
                                        bestDistance = distance
                                        bestId = id
                                    }
                                }

                                bestId?.let { onPlantClick(it) }
                            }
                        },
            ) {
                val gridStep = size.width / 5f
                for (index in 1..4) {
                    val x = gridStep * index
                    drawLine(pathColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 2f)
                }

                val horizontalStep = size.height / 4f
                for (index in 1..3) {
                    val y = horizontalStep * index
                    drawLine(pathColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 2f)
                }

                drawLine(
                    color = pathColor,
                    start = Offset(size.width * 0.08f, size.height * 0.76f),
                    end = Offset(size.width * 0.92f, size.height * 0.24f),
                    strokeWidth = 18f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = pathColor,
                    start = Offset(size.width * 0.16f, size.height * 0.2f),
                    end = Offset(size.width * 0.84f, size.height * 0.68f),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round,
                )

                plantas.take(12).forEach { planta ->
                    val position = markerOffsetFor(planta, size.width, size.height)
                    drawCircle(plantMarkerColor.copy(alpha = 0.25f), radius = 20f, center = position)
                    drawCircle(plantMarkerColor, radius = 10f, center = position)
                    drawCircle(Color.White, radius = 4f, center = position)
                }

                val userPosition = Offset(size.width * 0.5f, size.height * 0.5f)
                drawCircle(currentLocationColor.copy(alpha = 0.18f), radius = 44f, center = userPosition)
                drawCircle(currentLocationColor, radius = 18f, center = userPosition)
                drawCircle(Color.White, radius = 8f, center = userPosition)
                drawCircle(primary, radius = 28f, center = userPosition, style = Stroke(width = 4f))
            }

            Column(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${plantas.size} plantas marcadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            MapLegend(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                currentLocationColor = currentLocationColor,
                plantMarkerColor = plantMarkerColor,
            )
        }
    }
}

@Composable
private fun MapLegend(
    currentLocationColor: Color,
    plantMarkerColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendItem(color = currentLocationColor, label = "Você")
            LegendItem(color = plantMarkerColor, label = "Planta")
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun markerOffsetFor(
    planta: Planta,
    width: Float,
    height: Float,
): Offset {
    if (planta.latitude != null && planta.longitude != null) {
        val x = ((planta.longitude + 180.0) / 360.0).coerceIn(0.0, 1.0).toFloat()
        val y = ((90.0 - planta.latitude) / 180.0).coerceIn(0.0, 1.0).toFloat()
        return Offset(width * x, height * y)
    }

    val seed = "${planta.nome}-${planta.localizacao}".hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }
    val x = 0.12f + ((seed % 760) / 1000f)
    val y = 0.16f + (((seed / 1000) % 680) / 1000f)
    return Offset(width * x, height * y)
}
