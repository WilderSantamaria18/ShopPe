package com.idat.domain.repository

import com.idat.domain.model.Pedido
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    suspend fun savePedido(pedido: Pedido): Result<Unit>
    fun getPedidosByUser(userId: String): Flow<List<Pedido>>
}
