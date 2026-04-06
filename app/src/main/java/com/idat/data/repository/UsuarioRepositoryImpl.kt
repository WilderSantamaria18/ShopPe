package com.idat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.idat.domain.model.Usuario
import com.idat.domain.repository.UsuarioRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UsuarioRepository {

    override fun getUsuario(uid: String): Flow<Usuario?> = callbackFlow {
        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val usuario = snapshot?.toObject(Usuario::class.java)
                trySend(usuario)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun guardarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            firestore.collection("users").document(usuario.uid)
                .set(usuario)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
