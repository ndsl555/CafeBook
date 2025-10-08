package com.example.cafebook.di

import com.example.cafebook.Repository.BarRepository
import com.example.cafebook.Repository.CafeShopRepository
import com.example.cafebook.Repository.IBarRepository
import com.example.cafebook.Repository.ICafeShopRepository
import org.koin.dsl.module

val dataModule =
    module {
        includes(ioDispatcherModule, databaseModule, moshiModule)
        factory<ICafeShopRepository> { CafeShopRepository(get(), get(koinIO)) }
        factory<IBarRepository> { BarRepository(get(koinIO), get()) }
    }
