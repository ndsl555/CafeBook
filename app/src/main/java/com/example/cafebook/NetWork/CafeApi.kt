package com.example.cafebook.NetWork

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CafeApi {
    private const val BASE_URL = "https://cafenomad.tw/api/v1.2/"

    val retrofitService: CafeService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CafeService::class.java)
    }
}
