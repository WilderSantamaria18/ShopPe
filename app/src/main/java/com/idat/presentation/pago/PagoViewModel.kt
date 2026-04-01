package com.idat.presentation.pago

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.domain.model.Pedido
import com.idat.domain.repository.PedidoRepository
import com.idat.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PagoViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val productoRepository: ProductoRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    val cartItems = productoRepository.obtenerCarrito().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val totalAmount: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.precio * it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun procesarPago(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) {
                onError("El carrito está vacío")
                return@launch
            }

            val userId = auth.currentUser?.uid ?: ""
            val pedido = Pedido(
                id = "SP-${UUID.randomUUID().toString().take(6).uppercase()}",
                total = totalAmount.value,
                estado = "Pendiente",
                items = items,
                userId = userId
            )

            val resultSave = pedidoRepository.savePedido(pedido)
            if (resultSave.isSuccess) {
                productoRepository.vaciarCarrito()
                onSuccess()
            } else {
                onError("Error al guardar el pedido: ${resultSave.exceptionOrNull()?.message}")
            }
        }
    }
}
