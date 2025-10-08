package com.example.cafebook.di

import com.example.cafe.NearViewModel
import com.example.cafe.ViewModels.BarcodeViewModel
import com.example.cafebook.ViewModels.PocketViewModel
import com.example.cafebook.ViewModels.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModule: Module =
    module {
        includes(ioDispatcherModule, domainModule, moshiModule)
        viewModel { SearchViewModel(get(), get()) }
        viewModel { NearViewModel(get()) }
        viewModel { BarcodeViewModel(get(), get()) }
        viewModel { PocketViewModel(get(), get(), get(), get(), get()) }
    }
