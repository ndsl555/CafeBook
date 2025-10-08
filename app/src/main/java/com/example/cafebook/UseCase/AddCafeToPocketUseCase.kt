package com.example.cafebook.UseCase

import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddCafeToPocketUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<AddCafeToPocketUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        val cafeShop = parameters.cafeShopEntity
        addDb(cafeShop)
        return Result.Success(Unit)
    }

    private suspend fun addDb(cafeShopEntity: CafeShopEntity) {
        cafeShopRepository.insertCafe(cafeShopEntity)
    }

    data class Parameters(
        val cafeShopEntity: CafeShopEntity,
    )
}
