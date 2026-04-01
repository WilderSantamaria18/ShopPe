package com.idat.core.auth

object AdminAccess {
    // Reemplaza por el correo admin real.
    const val ADMIN_EMAIL = "yeffercastillovega24@gmail.com"

    fun isAdminEmail(email: String?): Boolean {
        return email?.trim()?.lowercase() == ADMIN_EMAIL.lowercase()
    }
}
