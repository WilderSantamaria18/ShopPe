package com.idat.presentation.direcciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.domain.model.Direccion
import com.idat.domain.repository.DireccionRepository
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DireccionesViewModel @Inject constructor(
    private val repository: DireccionRepository,
    private val auth: FirebaseAuth,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    val direcciones: StateFlow<List<Direccion>> = repository.getDirecciones(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ubicacionActual = MutableStateFlow("Detectando ubicación...")
    val ubicacionActual: StateFlow<String> = _ubicacionActual.asStateFlow()

    private val _estaCargandoUbicacion = MutableStateFlow(false)
    val estaCargandoUbicacion: StateFlow<Boolean> = _estaCargandoUbicacion.asStateFlow()

    fun saveDireccion(direccion: Direccion) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.saveDireccion(userId, direccion)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun deleteDireccion(direccionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteDireccion(userId, direccionId)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun setPredeterminada(direccionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.setPredeterminada(userId, direccionId)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _estaCargandoUbicacion.value = true
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    val address = getAddressFromLocation(location.latitude, location.longitude)
                    _ubicacionActual.value = address ?: "Ubicación no encontrada"
                } else {
                    _ubicacionActual.value = "Error al obtener ubicación"
                }
            } catch (e: SecurityException) {
                _ubicacionActual.value = "Permiso denegado"
            } catch (e: Exception) {
                _ubicacionActual.value = "Error: ${e.message}"
            } finally {
                _estaCargandoUbicacion.value = false
            }
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val street = addr.thoroughfare ?: ""
                val subLocal = addr.subLocality ?: ""
                val locality = addr.locality ?: ""
                if (street.isNotEmpty()) "$street, $locality" else locality
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
