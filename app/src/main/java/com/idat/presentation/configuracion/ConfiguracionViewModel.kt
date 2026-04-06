package com.idat.presentation.configuracion

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.idat.data.local.preferences.UserPreferencesManager
import com.idat.domain.model.Usuario
import com.idat.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val preferencesManager: UserPreferencesManager,
    private val usuarioRepository: UsuarioRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val storage = FirebaseStorage.getInstance()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario = _usuario.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation

    // Estados temporales para la ubicación detectada
    private val _ubicacionDetectada = MutableStateFlow<Pair<String, String>?>(null) // Pair(Distrito, Departamento)
    val ubicacionDetectada: StateFlow<Pair<String, String>?> = _ubicacionDetectada

    private var latitudActual: Double? = null
    private var longitudActual: Double? = null

    init {
        auth.currentUser?.let { user ->
            _email.value = user.email ?: ""
            cargarDatosUsuario(user.uid)
        }
        
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _email.value = user?.email ?: ""
            user?.let { cargarDatosUsuario(it.uid) }
        }

        viewModelScope.launch {
            preferencesManager.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    private fun cargarDatosUsuario(uid: String) {
        viewModelScope.launch {
            usuarioRepository.getUsuario(uid).collect { user ->
                _usuario.value = user
                latitudActual = user?.latitud
                longitudActual = user?.longitud
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual() {
        _isLoadingLocation.value = true
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                location?.let {
                    latitudActual = it.latitude
                    longitudActual = it.longitude
                    
                    val geocoder = Geocoder(context, Locale("es", "PE"))
                    
                    // Manejo de Geocoder para versiones nuevas y antiguas
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(it.latitude, it.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                // Priorizamos locality (Distrito) sobre subLocality (A.H./Urb)
                                val distrito = address.locality ?: address.subLocality ?: ""
                                
                                // Limpiamos "Provincia de" si aparece en el departamento
                                var departamento = address.adminArea ?: ""
                                if (departamento.contains("Provincia de", ignoreCase = true)) {
                                    departamento = departamento.replace("Provincia de", "", ignoreCase = true).trim()
                                }
                                
                                _ubicacionDetectada.value = Pair(distrito, departamento)
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            // Priorizamos locality (Distrito) sobre subLocality (A.H./Urb)
                            val distrito = address.locality ?: address.subLocality ?: ""
                            
                            var departamento = address.adminArea ?: ""
                            if (departamento.contains("Provincia de", ignoreCase = true)) {
                                departamento = departamento.replace("Provincia de", "", ignoreCase = true).trim()
                            }

                            _ubicacionDetectada.value = Pair(distrito, departamento)
                        }
                    }
                } ?: run {
                    _errorMessage.value = "No se pudo obtener la ubicación. Activa tu GPS."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener ubicación: ${e.localizedMessage}"
            } finally {
                _isLoadingLocation.value = false
            }
        }
    }

    fun subirFotoPerfil(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        _isUploading.value = true
        
        val storageRef = storage.reference.child("perfiles/$uid.jpg")
        
        viewModelScope.launch {
            try {
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                
                val currentUsuario = _usuario.value
                if (currentUsuario != null) {
                    val usuarioActualizado = currentUsuario.copy(fotoUrl = downloadUrl)
                    usuarioRepository.guardarUsuario(usuarioActualizado)
                    _successMessage.value = "Foto de perfil actualizada"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al subir imagen: ${e.localizedMessage}"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun guardarPerfil(
        nombre: String,
        apellido: String,
        dni: String,
        telefono: String,
        direccion: String,
        distrito: String,
        departamento: String
    ) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val email = auth.currentUser?.email ?: ""
            val fotoUrlActual = _usuario.value?.fotoUrl ?: ""
            
            val nuevoUsuario = Usuario(
                uid = uid,
                nombre = nombre,
                apellido = apellido,
                email = email,
                dni = dni,
                telefono = telefono,
                direccion = direccion,
                distrito = distrito,
                departamento = departamento,
                fotoUrl = fotoUrlActual,
                latitud = latitudActual,
                longitud = longitudActual
            )
            
            usuarioRepository.guardarUsuario(nuevoUsuario).fold(
                onSuccess = {
                    _successMessage.value = "Perfil actualizado correctamente"
                },
                onFailure = {
                    _errorMessage.value = "Error al guardar perfil: ${it.message}"
                }
            )
        }
    }

    fun cambiarPassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        _successMessage.value = "Contraseña cambiada correctamente"
                                    } else {
                                        _errorMessage.value = updateTask.exception?.message ?: "Error al cambiar contraseña"
                                    }
                                }
                        } else {
                            _errorMessage.value = "Contraseña actual incorrecta"
                        }
                    }
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
        _ubicacionDetectada.value = null
    }
}
