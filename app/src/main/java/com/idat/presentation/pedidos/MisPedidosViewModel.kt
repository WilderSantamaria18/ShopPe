package com.idat.presentation.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.domain.model.Pedido
import com.idat.domain.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisPedidosViewModel @Inject constructor(
    private val repository: PedidoRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Todos")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        val user = auth.currentUser
        val userEmail = user?.email
        _isAdmin.value = userEmail == "admin@shoppe.com"

        if (_isAdmin.value) {
            // Admin: Carga todos los pedidos
            repository.getAllPedidos()
                .retryWhen { cause, attempt ->
                    if (cause is java.io.IOException || cause.message?.contains("UNAVAILABLE") == true) {
                        val delayTime = (attempt + 1) * 2000L
                        kotlinx.coroutines.delay(minOf(delayTime, 30000L))
                        true
                    } else {
                        false
                    }
                }
                .catch { handleException(it) }
                .onEach { _pedidos.value = it }
                .launchIn(viewModelScope)
        } else {
            // Cliente: Carga solo sus pedidos
            user?.uid?.let { userId ->
                repository.getPedidosByUser(userId)
                    .retryWhen { cause, attempt ->
                        if (cause is java.io.IOException || cause.message?.contains("UNAVAILABLE") == true) {
                            val delayTime = (attempt + 1) * 2000L
                            kotlinx.coroutines.delay(minOf(delayTime, 30000L))
                            true
                        } else {
                            false
                        }
                    }
                    .catch { handleException(it) }
                    .onEach { _pedidos.value = it }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun handleException(e: Throwable) {
        if (e.message?.contains("PERMISSION_DENIED") == true) {
            _uiError.value = "Permisos de acceso denegados. Administrador requerido o reglas de Firebase desactualizadas."
        } else {
            _uiError.value = "Error: ${e.localizedMessage}"
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    val filteredPedidos = combine(pedidos, selectedFilter) { pedidos, filter ->
        if (filter == "Todos") pedidos
        else pedidos.filter { it.estado.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGastoMes = pedidos.map { list ->
        list.sumOf { it.total }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: String) {
        viewModelScope.launch {
            val result = repository.updatePedidoStatus(pedidoId, nuevoEstado)
            if (result.isFailure) {
                _uiError.value = "No se pudo actualizar el estado: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}
