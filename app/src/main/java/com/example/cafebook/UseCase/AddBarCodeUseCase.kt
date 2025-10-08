package com.example.cafebook.UseCase

import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Repository.IBarRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.Result.Success
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddBarCodeUseCase(val barRepository: IBarRepository, dispatcher: CoroutineDispatcher) :
    UseCase<AddBarCodeUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        barRepository.insertBar(parameters.barEntity)
        return Success(Unit)
    }

    data class Parameters(
        val barEntity: BarEntity,
    )
}
