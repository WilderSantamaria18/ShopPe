package com.idat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.idat.domain.model.Pedido
import com.idat.domain.repository.PedidoRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PedidoRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PedidoRepository {

    override suspend fun savePedido(pedido: Pedido): Result<Unit> = try {
        firestore.collection("pedidos").document(pedido.id).set(pedido).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getPedidosByUser(userId: String): Flow<List<Pedido>> = callbackFlow {
        val subscription = firestore.collection("pedidos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Solo retornar, permitiendo que el SDK de Firestore maneje reintentos internamente
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(Pedido::class.java)
                    trySend(pedidos)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getAllPedidos(): Flow<List<Pedido>> = callbackFlow {
        val subscription = firestore.collection("pedidos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Solo retornar, permitiendo que el SDK de Firestore maneje reintentos internamente
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(Pedido::class.java)
                    trySend(pedidos)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getPedidoById(pedidoId: String): Flow<Pedido?> = callbackFlow {
        val subscription = firestore.collection("pedidos").document(pedidoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Solo retornar, permitiendo que el SDK de Firestore maneje reintentos internamente
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val pedido = snapshot.toObject(Pedido::class.java)
                    trySend(pedido)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updatePedidoStatus(pedidoId: String, newStatus: String): Result<Unit> = try {
        firestore.collection("pedidos").document(pedidoId)
            .update("estado", newStatus)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
