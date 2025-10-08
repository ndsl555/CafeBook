package com.example.cafebook.UseCase

import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.Result.Success
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class DeleteAllFromPocketUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(dispatcher) {
    override suspend fun execute(parameter: Unit): Result<Unit> {
        cafeShopRepository.deleteAll()
        return Success(Unit)
    }
}
