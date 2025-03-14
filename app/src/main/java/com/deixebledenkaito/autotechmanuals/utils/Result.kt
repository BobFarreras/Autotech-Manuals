package com.deixebledenkaito.autotechmanuals.utils

// utils/Result.kt
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val type: AuthErrorType, val exception: Exception? = null) : Result<Nothing>()
}

// utils/AuthErrorType.kt
enum class AuthErrorType(val description: String) {
    INVALID_CREDENTIALS("Credencials incorrectes"),
    USER_NOT_FOUND("Usuari no trobat"),
    WEAK_PASSWORD("Contrasenya feble"),
    INVALID_CODE("Codi invàlid"),
    UNKNOWN_ERROR("Error desconegut")
}