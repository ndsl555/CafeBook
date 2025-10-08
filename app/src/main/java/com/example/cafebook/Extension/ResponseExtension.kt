package com.example.cafebook.Extension

import com.example.cafebook.Utils.ApiException
import com.example.cafebook.Utils.ApiNullableResponse
import com.example.cafebook.Utils.Result
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T, R> apiCall(
    call: suspend () -> Response<T>,
    toResult: (T) -> R,
): Result<R> {
    return try {
        val response = call.invoke()

        if (!response.isSuccessful) {
            Result.Error(
                ApiException(
                    response.errorBody().toString(),
                    response.code(),
                ),
            )
        } else {
            val body = response.body()
            if (body == null) {
                Result.Error(NullBodyException())
            } else {
                Result.Success(toResult(body))
            }
        }
    } catch (e: SocketTimeoutException) {
        Result.Error(e)
    } catch (e: IOException) {
        Result.Error(e)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Error(e)
    }
}

suspend fun <T> apiCall(call: suspend () -> Response<T>): Result<Unit> {
    return apiCall(call, {})
}

inline fun <T, R> Response<T>.toResult(crossinline call: Result<T>.() -> Result<R>): Result<R> {
    return call.invoke(this.toResult())
}

fun <T> Response<T>.toResult(): Result<T> {
    val result =
        try {
            if (!isSuccessful) {
                if (this.code() == 401) {
                    val type = Types.newParameterizedType(ApiNullableResponse::class.java, String::class.java)
                    val adapter = Moshi.Builder().build().adapter<ApiNullableResponse<String>>(type)
                    val body = errorBody()
                    if (body == null) {
                        Result.Error(
                            ApiException(
                                errorBody().toString(),
                                code(),
                            ),
                        )
                    } else {
                        val apiResponse = adapter.fromJson(body.source())
                        Result.Error(
                            ApiException(
                                apiResponse?.errorMessage ?: "",
                                code(),
                                apiResponse?.result ?: -1,
                            ),
                        )
                    }
                } else {
                    Result.Error(
                        ApiException(
                            this.errorBody()?.string() ?: errorBody().toString(),
                            code(),
                        ),
                    )
                }
            } else {
                val body = body()
                if (body == null) {
                    Result.Error(NullBodyException())
                } else {
                    Result.Success(body)
                }
            }
        } catch (e: JsonEncodingException) {
            println("Api Response JsonEncodingException ${e.message}")
            Result.Error(e)
        } catch (e: SocketTimeoutException) {
            Result.Error(e)
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    return result
}

typealias NullBodyException = NullPointerException
