package com.idat.presentation.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.domain.model.Pedido
import com.idat.domain.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    init {
        auth.currentUser?.uid?.let { userId ->
            repository.getPedidosByUser(userId)
                .onEach { _pedidos.value = it }
                .launchIn(viewModelScope)
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
}
