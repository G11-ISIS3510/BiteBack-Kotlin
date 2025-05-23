package com.kotlin.biteback.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.biteback.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition

@Composable
fun BusinessMap(
    modifier: Modifier = Modifier,
    businessLat: Double,
    businessLng: Double
) {
    val context = LocalContext.current

    remember { MapLibre.getInstance(context) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=oVWS2FelneSuNcAhLzee"

    AndroidView(
        factory = {
            MapView(it).apply {
                getMapAsync { map ->
                    map.setStyle(styleUrl) {
                        showBusinessMarker(map, businessLat, businessLng)
                        enableUserLocation(context, map, fusedLocationClient)
                        CoroutineScope(Dispatchers.IO).launch {
                            cacheMapTiles(context, businessLat, businessLng)
                        }
                    }
                }
            }
        },
        modifier = modifier
    )
}




private fun showBusinessMarker(map: MapLibreMap, lat: Double, lng: Double) {
    val position = LatLng(lat, lng)
    map.addMarker(
        MarkerOptions()
            .position(position)
            .title("Ubicación del negocio")
    )

    map.cameraPosition = CameraPosition.Builder()
        .target(position)
        .zoom(14.0)
        .build()
}

@SuppressLint("MissingPermission")
private fun enableUserLocation(
    context: Context,
    map: MapLibreMap,
    fusedLocationClient: FusedLocationProviderClient
) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PermissionChecker.PERMISSION_GRANTED

    if (!hasPermission) return

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let {
            val userPosition = LatLng(it.latitude, it.longitude)

            val iconDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_user_marker)
            val iconBitmap: Bitmap? = iconDrawable?.toBitmap(30, 30)

            if (iconBitmap != null) {
                val icon: Icon = IconFactory.getInstance(context).fromBitmap(iconBitmap)

                map.addMarker(
                    MarkerOptions()
                        .position(userPosition)
                        .title("Tú estás aquí")
                        .icon(icon)
                )
            }
        }
    }
}

private fun cacheMapTiles(context: Context, lat: Double, lng: Double) {
    val offlineManager = OfflineManager.getInstance(context)

    val bounds = LatLngBounds.Builder()
        .include(LatLng(lat + 0.01, lng + 0.01))
        .include(LatLng(lat - 0.01, lng - 0.01))
        .build()

    val definition = OfflineTilePyramidRegionDefinition(
        "https://api.maptiler.com/maps/streets/style.json?key=oVWS2FelneSuNcAhLzee",
        bounds,
        10.0,
        16.0,
        context.resources.displayMetrics.density
    )

    val metadata = byteArrayOf()

    offlineManager.createOfflineRegion(definition, metadata,
        object : OfflineManager.CreateOfflineRegionCallback {
            override fun onCreate(region: OfflineRegion) {
                region.setDownloadState(OfflineRegion.STATE_ACTIVE)
            }

            override fun onError(error: String) {
                Log.e("MapLibre", "Error creando región offline: $error")
            }
        }
    )
}
