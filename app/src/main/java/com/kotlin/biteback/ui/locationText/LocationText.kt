package com.kotlin.biteback.ui.locationText

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kotlin.biteback.data.repository.LocationRepository
import com.kotlin.biteback.ui.home.LocationViewModelFactory
import com.kotlin.biteback.viewModel.LocationViewModel
import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LocationText(locationRepository: LocationRepository) {
    val colors = MaterialTheme.colorScheme
    val viewModel: LocationViewModel = viewModel(factory = LocationViewModelFactory(locationRepository))
    val address by viewModel.address.collectAsState()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            viewModel.fetchLocation()
        }
    }

    val addressParts = address.split(",")
    val location = addressParts[0].trim()
    Column(
        modifier = Modifier
    ) {
        Text(
            text = "Hola, Danny",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp)

        )
        if (locationPermissionState.status.isGranted) {
            Text(text = location,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground)
        } else {
            Text(text = "Permiso no concedido",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground)
        }
    }



}