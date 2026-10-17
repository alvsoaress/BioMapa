package org.example.biomapa

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
actual fun LocationPickerCard(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier,
) {
    if (LocalInspectionMode.current) {
        MapPreviewPlaceholder(modifier)
        return
    }

    val context = LocalContext.current
    var locationPermissionGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    var currentLocation by remember { mutableStateOf(lastKnownGeoPoint(context)) }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { result ->
            locationPermissionGranted = result.values.any { it }
        }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(LocationPermissions)
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            currentLocation = lastKnownGeoPoint(context)
        }
    }

    val initialPoint =
        remember(selectedLatitude, selectedLongitude, currentLocation) {
            GeoPoint(selectedLatitude ?: currentLocation?.latitude ?: -23.5505, selectedLongitude ?: currentLocation?.longitude ?: -46.6333)
        }
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
                controller.setCenter(initialPoint)
            }
        }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { mapView },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                update = { view ->
                    val selectedPoint =
                        if (selectedLatitude != null && selectedLongitude != null) {
                            GeoPoint(selectedLatitude, selectedLongitude)
                        } else {
                            initialPoint
                        }

                    view.overlays.clear()
                    view.overlays.add(
                        MapEventsOverlay(
                            object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                                    onLocationSelected(point.latitude, point.longitude)
                                    view.controller.animateTo(point)
                                    return true
                                }

                                override fun longPressHelper(point: GeoPoint): Boolean = false
                            },
                        ),
                    )

                    if (locationPermissionGranted) {
                        view.overlays.add(
                            MyLocationNewOverlay(GpsMyLocationProvider(context), view).apply {
                                enableMyLocation()
                                runOnFirstFix {
                                    view.post {
                                        currentLocation = myLocation
                                        if (selectedLatitude == null || selectedLongitude == null) {
                                            myLocation?.let { location ->
                                                view.controller.setZoom(17.0)
                                                view.controller.animateTo(location)
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    }

                    if (selectedLatitude != null && selectedLongitude != null) {
                        view.overlays.add(
                            Marker(view).apply {
                                position = selectedPoint
                                title = "Localização da planta"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            },
                        )
                    }

                    view.controller.setCenter(selectedPoint)
                    view.invalidate()
                },
            )

            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                tonalElevation = 2.dp,
            ) {
                Text(
                    text =
                        if (selectedLatitude != null && selectedLongitude != null) {
                            "Localização marcada"
                        } else if (locationPermissionGranted) {
                            "Toque no mapa para marcar a planta perto de você"
                        } else {
                            "Permita a localização e toque no mapa para marcar"
                        },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
