package com.idat.presentation.gestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idat.domain.model.Usuario
import com.idat.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AdminUsuariosViewModel @Inject constructor(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        _isLoading.value = true
        _errorMessage.value = null
        repository.getUsuarios()
            .onEach { 
                _usuarios.value = it 
                _isLoading.value = false
            }
            .catch { e ->
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
}
