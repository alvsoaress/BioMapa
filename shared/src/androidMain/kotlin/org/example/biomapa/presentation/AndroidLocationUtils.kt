package org.example.biomapa.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import java.io.File

// Centro aproximado do campus central da UFRN, usado quando não há planta nem localização.
internal val CampusCenter = GeoPoint(-5.8400, -35.2000)

// Sem um caminho explícito o osmdroid tenta gravar o cache de tiles em "/tiles" e o mapa fica em branco.
// Os servidores do OpenStreetMap bloqueiam (403) User-Agents genéricos como "org.example.biomapa".
internal fun configureOsmdroid(context: Context) {
    Configuration.getInstance().apply {
        userAgentValue = OsmUserAgent
        osmdroidBasePath = File(context.cacheDir, "osmdroid")
        osmdroidTileCache = File(osmdroidBasePath, "tiles")
    }
}

internal val LocationPermissions =
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

internal fun hasLocationPermission(context: Context): Boolean {
    return LocationPermissions.any { permission ->
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}

internal fun lastKnownGeoPoint(context: Context): GeoPoint? {
    if (!hasLocationPermission(context)) return null

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)
    val location =
        providers
            .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { it.time }

    return location?.let { GeoPoint(it.latitude, it.longitude) }
}

private const val OsmUserAgent = "BioMapa-UFRN/1.0 (+https://github.com/alvsoaress/BioMapa)"
