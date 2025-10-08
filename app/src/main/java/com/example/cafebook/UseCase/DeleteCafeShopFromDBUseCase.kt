package com.example.cafebook.UseCase

import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.Result.Success
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class DeleteCafeShopFromDBUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<DeleteCafeShopFromDBUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        cafeShopRepository.deleteByName(parameters.cafeShopName)
        return Success(Unit)
    }

    data class Parameters(
        val cafeShopName: String,
    )
}
