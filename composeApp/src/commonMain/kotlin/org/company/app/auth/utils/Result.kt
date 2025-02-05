package org.company.app.auth.utils

sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val message: String? = null,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : Result<Nothing>()
}