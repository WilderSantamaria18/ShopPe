package com.idat.domain.repository

import com.idat.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun getUsuario(uid: String): Flow<Usuario?>
    suspend fun guardarUsuario(usuario: Usuario): Result<Unit>
}
