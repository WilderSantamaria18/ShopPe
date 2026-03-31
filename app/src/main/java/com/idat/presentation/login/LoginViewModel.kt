package com.idat.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** LOGIN CON EMAIL Y CONTRASEÑA **/
    fun iniciarSesion(email: String, password: String) {
        // Validación de campos vacíos
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Por favor, ingresa tu correo y contraseña."
            return
        }

        viewModelScope.launch {
            _errorMessage.value = null // Limpiar error previo
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginExitoso.value = true
                    } else {
                        // Analizar el tipo de excepción para dar un mensaje específico
                        val error = when (task.exception) {
                            is FirebaseAuthInvalidUserException ->
                                "El correo electrónico no se encuentra registrado."
                            is FirebaseAuthInvalidCredentialsException ->
                                "La contraseña es incorrecta. Por favor, inténtalo de nuevo."
                            else ->
                                "Ocurrió un error inesperado. Revisa tu conexión o inténtalo más tarde."
                        }
                        _errorMessage.value = error
                    }
                }
        }
    }

    /** LOGIN CON GOOGLE (Firebase Credential) **/
    fun loginWithGoogleCredential(result: SignInResult) {
        viewModelScope.launch {
            _errorMessage.value = null // Limpiar error previo
            result.credential?.let { credential ->
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginExitoso.value = true
                        } else {
                            _errorMessage.value = "Error al autenticar con Google. Inténtalo de nuevo."
                        }
                    }
            } ?: run {
                _errorMessage.value = result.errorMessage
                    ?: "No se pudieron obtener las credenciales de Google."
            }
        }
    }
}
