package com.example.cafebook.di

import com.squareup.moshi.Moshi
import org.koin.dsl.module

val moshiModule =
    module {
        single { Moshi.Builder() }
    }
