package com.deixebledenkaito.autotechmanuals.data.network.auth

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val type: AuthErrorType) : Result<Nothing>()
}

enum class AuthErrorType {
    INVALID_CREDENTIALS, // Credencials incorrectes
    USER_NOT_FOUND, // Usuari no trobat
    NETWORK_ERROR, // Error de xarxa
    UNKNOWN_ERROR // Error desconegut
}