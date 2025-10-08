package com.example.cafebook.UseCase

import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetAverageUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, CafeAverages>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<CafeAverages> {
        return cafeShopRepository.getAverages()
    }
}
