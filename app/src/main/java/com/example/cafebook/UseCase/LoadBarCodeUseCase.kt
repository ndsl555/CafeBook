package com.example.cafebook.UseCase

import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Repository.IBarRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LoadBarCodeUseCase(val barRepository: IBarRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, BarEntity>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<BarEntity> {
        return barRepository.getLatestBar()
    }
}
