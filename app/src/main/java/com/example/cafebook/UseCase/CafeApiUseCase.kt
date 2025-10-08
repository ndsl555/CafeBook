package com.example.cafebook.UseCase

import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.NetWork.ICafeRemoteDataSource
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class CafeApiUseCase(
    private val cafeRemoteDataSource: ICafeRemoteDataSource,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, List<CafeShopEntity>>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<List<CafeShopEntity>> {
        return cafeRemoteDataSource.fetchCafes()
    }
}
