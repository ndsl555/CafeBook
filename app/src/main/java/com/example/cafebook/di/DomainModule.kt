package com.example.cafebook.di

import com.example.cafebook.UseCase.AddBarCodeUseCase
import com.example.cafebook.UseCase.AddCafeToPocketUseCase
import com.example.cafebook.UseCase.CafeApiUseCase
import com.example.cafebook.UseCase.DeleteAllFromPocketUseCase
import com.example.cafebook.UseCase.DeleteCafeShopFromDBUseCase
import com.example.cafebook.UseCase.FilterCafeFromDBUseCase
import com.example.cafebook.UseCase.GetAverageUseCase
import com.example.cafebook.UseCase.GetUniqueCityFromDBUseCase
import com.example.cafebook.UseCase.LoadBarCodeUseCase
import org.koin.dsl.module

val domainModule =
    module {
        includes(ioDispatcherModule, dataModule, moshiModule)
        factory { AddCafeToPocketUseCase(get(), get(koinIO)) }
        factory { GetUniqueCityFromDBUseCase(get(), get(koinIO)) }
        factory { FilterCafeFromDBUseCase(get(), get(koinIO)) }
        factory { DeleteAllFromPocketUseCase(get(), get(koinIO)) }
        factory { DeleteCafeShopFromDBUseCase(get(), get(koinIO)) }
        factory { AddBarCodeUseCase(get(), get(koinIO)) }
        factory { LoadBarCodeUseCase(get(), get(koinIO)) }
        factory { CafeApiUseCase(get(), get(koinIO)) }
        factory { GetAverageUseCase(get(), get(koinIO)) }
    }
