package org.company.app.auth.utils


sealed class UiEvent<out T> {
    object Loading : UiEvent<Nothing>()
    data class Success<T>(val data: T) : UiEvent<T>()
    data class Failure(val error: String) : UiEvent<Nothing>()
}
