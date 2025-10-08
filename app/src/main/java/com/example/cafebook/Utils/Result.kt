package com.example.cafebook.Utils

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data=$data]"
            is Error -> "Error [exception=$exception]"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null

val Result<*>.fail
    get() = this is Result.Error

val <T> Result<T>.dataIfExist: T?
    get() = (this as? Result.Success)?.data

val <T> Result<T>.existData: T
    get() = (this as Result.Success).data

val Result<*>.throwException
    get() = (this as Result.Error).exception

inline fun <reified T, reified R> Result<T>.successOrNot(successOr: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> {
            val result = successOr.invoke(data)
            Result.Success(result)
        }
        is Result.Error -> {
            this
        }
        else -> throw IllegalStateException()
    }
}

inline fun <reified T, reified R> Result<T>.successResultOrThrow(
    throwable: (Throwable) -> Unit = {},
    successOr: (T) -> R,
): R {
    return when (this) {
        is Result.Success -> {
            val result = successOr.invoke(data)
            result
        }
        is Result.Error -> {
            throwable.invoke(exception)
            throw exception
        }
    }
}

inline fun <reified T> Result<T>.guardResult(throwable: (Throwable) -> Unit = {}) =
    when (this) {
        is Result.Success -> data
        is Result.Error -> {
            throwable.invoke(exception)
            throw exception
        }
    }

inline fun <T, R> T.runCatching(block: T.() -> R): Result<R> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
