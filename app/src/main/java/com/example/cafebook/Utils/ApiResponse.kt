package com.example.cafebook.Utils

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class ApiNullableResponse<T>(
    open val `data`: T? = null,
    @Json(name = "errMsg")
    open val errorMessage: String,
    open val result: Int,
)
