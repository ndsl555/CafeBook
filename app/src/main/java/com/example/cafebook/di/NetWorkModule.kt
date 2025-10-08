package com.example.cafebook.di

import com.example.cafebook.NetWork.CafeService
import com.example.cafebook.NetWork.ICafeRemoteDataSource
import com.example.cafebook.Repository.CafeRemoteDataSource
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule =
    module {
        single {
            Retrofit.Builder()
                .baseUrl("https://cafenomad.tw/api/v1.2/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
        }

        factory {
            get<Retrofit>().create(CafeService::class.java)
        }

        factory<ICafeRemoteDataSource> { CafeRemoteDataSource(get(), get(koinIO)) }
    }
