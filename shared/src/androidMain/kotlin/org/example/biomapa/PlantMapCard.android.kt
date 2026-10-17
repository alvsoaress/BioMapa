package org.example.biomapa

import android.content.res.Resources
import android.graphics.drawable.InsetDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt

@Composable
actual fun PlantMapCard(
    plantas: List<Planta>,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier,
) {
    if (LocalInspectionMode.current) {
        MapPreviewPlaceholder(modifier)
        return
    }

    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { result ->
            locationPermissionGranted = result.values.any { it }
        }

    val center = remember(plantas) { plantas.firstWithCoordinates() ?: CampusCenter }
    val mapView =
        remember {
            Configuration.getInstance().userAgentValue = context.packageName
            val tileProvider =
                MapTileProviderBasic(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                }
            MapView(context, tileProvider).apply {
                setMultiTouchControls(true)
                controller.setZoom(17.0)
            }
        }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(LocationPermissions)
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { mapView },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp)),
                update = { view ->
                    view.controller.setCenter(GeoPoint(center.latitude, center.longitude))
                    view.overlays.clear()

                    if (locationPermissionGranted) {
                        view.overlays.add(
                            MyLocationNewOverlay(GpsMyLocationProvider(context), view).apply {
                                enableMyLocation()
                                enableFollowLocation()
                                runOnFirstFix {
                                    view.post {
                                        myLocation?.let { location ->
                                            view.controller.setZoom(17.0)
                                            view.controller.animateTo(location)
                                        }
                                    }
                                }
                            },
                        )
                    }

                    plantas.forEach { planta ->
                        val latitude = planta.latitude ?: return@forEach
                        val longitude = planta.longitude ?: return@forEach
                        view.overlays.add(
                            Marker(view).apply {
                                position = GeoPoint(latitude, longitude)
                                title = planta.nome
                                snippet = planta.localizacao
                                withMinimumTouchTarget()
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                setOnMarkerClickListener { _, _ ->
                                    planta.id?.let { onPlantClick(it) }
                                    true
                                }
                            },
                        )
                    }
                    view.invalidate()
                },
            )

            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 2.dp,
            ) {
                Text(
                    text = if (locationPermissionGranted) "OpenStreetMap | Você está aqui" else "OpenStreetMap",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

// Preview do IDE não tem Activity (permissões) nem suporta o MapView do osmdroid.
@Composable
internal fun MapPreviewPlaceholder(modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Mapa (indisponível no preview)",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// O ícone padrão do osmdroid tem 27x48dp. Uma margem transparente nas laterais
// leva o alvo de toque a 48x48dp, já que o toque no marcador usa os limites do ícone.
private fun Marker.withMinimumTouchTarget() {
    val base = icon ?: return
    val minSize = (MinTouchTargetDp * Resources.getSystem().displayMetrics.density).roundToInt()
    val horizontal = ((minSize - base.intrinsicWidth) / 2).coerceAtLeast(0)
    val top = (minSize - base.intrinsicHeight).coerceAtLeast(0)
    icon = InsetDrawable(base, horizontal, top, horizontal, 0)
}

private const val MinTouchTargetDp = 48

private data class MapPoint(val latitude: Double, val longitude: Double)

private val CampusCenter = MapPoint(latitude = -23.5505, longitude = -46.6333)

private fun List<Planta>.firstWithCoordinates(): MapPoint? {
    val planta = firstOrNull { it.latitude != null && it.longitude != null } ?: return null
    return MapPoint(latitude = planta.latitude!!, longitude = planta.longitude!!)
}
