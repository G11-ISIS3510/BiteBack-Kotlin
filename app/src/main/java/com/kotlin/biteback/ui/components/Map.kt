package com.kotlin.biteback.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import com.kotlin.biteback.R

@Composable
fun BusinessMap(
    modifier: Modifier = Modifier,
    businessLat: Double,
    businessLng: Double
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    remember { MapLibre.getInstance(context) }

    AndroidView(
        factory = {
            MapView(it).apply {
                getMapAsync { map ->
                    map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=oVWS2FelneSuNcAhLzee") {
                        showBusinessMarker(map, businessLat, businessLng)
                        enableUserLocation(context, map, fusedLocationClient)
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
