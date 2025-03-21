package com.kotlin.biteback.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.location.Location


class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _address = MutableStateFlow("Obteniendo ubicación...")
    val address: StateFlow<String> = _address

    fun fetchLocation() {
        viewModelScope.launch {
            try {
                val location: Location? = locationRepository.getLastKnownLocation()
                if (location != null) {
                    _address.value = locationRepository.getAddressFromLocation(location.latitude, location.longitude)
                } else {
                    _address.value = "Ubicación no disponible"
                }
            } catch (e: SecurityException) {
                _address.value = "Permiso denegado. No se puede obtener la ubicación."
            } catch (e: Exception) {
                _address.value = "Error al obtener ubicación: ${e.message}"
            }
        }
    }
}
