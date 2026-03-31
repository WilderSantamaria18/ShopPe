package com.idat.presentation.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** REGISTRO CON EMAIL Y CONTRASEÑA **/
    fun registrarUsuario(email: String, password: String, confirmarPassword: String) {
        viewModelScope.launch {
            // Validaciones
            if (email.isBlank() || password.isBlank()) {
                _errorMessage.value = "Por favor completa todos los campos"
                return@launch
            }

            if (password != confirmarPassword) {
                _errorMessage.value = "Las contraseñas no coinciden"
                return@launch
            }

            if (password.length < 6) {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                return@launch
            }

            // Crear usuario en Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _registroExitoso.value = true
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Error al registrar usuario"
                    }
                }
        }
    }

    /** REGISTRO CON GOOGLE **/
    fun registrarConGoogle(idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _registroExitoso.value = true
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Error al registrar con Google"
                    }
                }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}
