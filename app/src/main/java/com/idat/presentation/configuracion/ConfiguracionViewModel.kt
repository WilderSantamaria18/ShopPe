package com.idat.presentation.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.idat.data.local.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    init {
        _email.value = auth.currentUser?.email ?: ""
        viewModelScope.launch {
            preferencesManager.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    fun actualizarEmail(newEmail: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                user.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _email.value = newEmail
                            _successMessage.value = "Email actualizado correctamente"
                        } else {
                            _errorMessage.value = task.exception?.message ?: "Error al actualizar email"
                        }
                    }
            }
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
    }
}
