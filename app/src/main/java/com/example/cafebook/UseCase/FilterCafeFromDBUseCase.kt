package com.example.cafebook.UseCase

import android.util.Log
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Repository.ICafeShopRepository
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.UseCase
import com.example.cafebook.ViewModels.PocketViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FilterCafeFromDBUseCase(
    private val cafeShopRepository: ICafeShopRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UseCase<FilterCafeFromDBUseCase.Parameters, List<CafeShopEntity>>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<List<CafeShopEntity>> {
        Log.d("FilterCafeUseCase", "Filter params: ${parameters.filterParams}")

        val result =
            cafeShopRepository.getFilteredCafes(
                wifiValue = parameters.filterParams.wifi,
                seatValue = parameters.filterParams.seat,
                quietValue = parameters.filterParams.quiet,
                tastyValue = parameters.filterParams.tasty,
                cheapValue = parameters.filterParams.cheap,
                musicValue = parameters.filterParams.music,
                cityFilter = parameters.filterParams.city,
                searchQuery = null,
            )

        Log.d("FilterCafeUseCase", "Result count: ${(result as? Result.Success)?.data?.size ?: "error"}")

        return result
    }

    data class Parameters(val filterParams: PocketViewModel.FilterParams)
}
