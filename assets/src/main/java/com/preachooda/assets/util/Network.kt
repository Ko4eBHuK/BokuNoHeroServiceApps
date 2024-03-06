package com.preachooda.assets.util

import kotlinx.coroutines.flow.flow
import retrofit2.Response

data class NetworkCallState<T>(
    val status: Status,
    val data: T? = null,
    val message: String = ""
) {

    companion object {
        fun <T> loading(message: String) = NetworkCallState<T>(
            status = Status.LOADING,
            message = message
        )

        fun <T> error(message: String) = NetworkCallState<T>(
            status = Status.ERROR,
            message = message
        )

        fun <T> success(data: T, message: String = "") = NetworkCallState(
            status = Status.SUCCESS,
            data = data,
            message = message
        )
    }
}

suspend fun <T> simpleNetworkCallFlow(
    call: suspend () -> Response<T>,
    loadingMessage: String,
    errorMessage: String,
    exceptionMessage: String
) = flow {
    emit(NetworkCallState.loading(loadingMessage))
    try {
        val response = call()
        if (response.isSuccessful) {
            emit(NetworkCallState.success(response.body()))
        } else {
            emit(NetworkCallState.error("$errorMessage, код: ${response.code()}"))
        }
    } catch (e: Exception) {
        emit(NetworkCallState.error("$exceptionMessage: ${e.message}"))
    }
}
