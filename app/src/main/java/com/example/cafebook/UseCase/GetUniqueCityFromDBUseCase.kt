package com.example.cafebook.UseCase

import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetUniqueCityFromDBUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, List<String>>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<List<String>> {
        return cafeShopRepository.getUniqueCities()
    }
}
