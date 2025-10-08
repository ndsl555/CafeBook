package com.example.cafebook.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ioDispatcherModule =
    module {
        single(koinIO) {
            Dispatchers.IO
        }
        single { Dispatchers.Main }
    }
val koinIO get() = named("IO")
