package com.example.cafebook.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.UseCase.*
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.invoke
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PocketViewModel(
    private val filterCafeFromDBUseCase: FilterCafeFromDBUseCase,
    private val getUniqueCityFromDBUseCase: GetUniqueCityFromDBUseCase,
    private val deleteAllFromPocketUseCase: DeleteAllFromPocketUseCase,
    private val deleteCafeShopFromDBUseCase: DeleteCafeShopFromDBUseCase,
    private val getAverageUseCase: GetAverageUseCase,
) : ViewModel() {
    private val _city = MutableStateFlow<String?>(null)
    private val _wifi = MutableStateFlow(0)
    private val _seat = MutableStateFlow(0)
    private val _quiet = MutableStateFlow(0)
    private val _tasty = MutableStateFlow(0)
    private val _cheap = MutableStateFlow(0)
    private val _music = MutableStateFlow(0)

    private val _bag = MutableStateFlow<CafeAverages>(CafeAverages(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
    val bag: StateFlow<CafeAverages> = _bag.asStateFlow()

    private val _filteredCafes = MutableStateFlow<List<CafeShopEntity>>(emptyList())
    val filteredCafes: StateFlow<List<CafeShopEntity>> = _filteredCafes.asStateFlow()

    private val _cityList = MutableStateFlow<List<String>>(emptyList())
    val cityList: StateFlow<List<String>> = _cityList.asStateFlow()

    init {
        triggerFilter() // 初始化時先查一次
        getAverage()
    }

    // 各 setter：更新值後馬上觸發查詢
    fun setCity(city: String?) {
        _city.value = city
        triggerFilter()
    }

    fun setWifi(value: Int) {
        _wifi.value = value
        triggerFilter()
    }

    fun setSeat(value: Int) {
        _seat.value = value
        triggerFilter()
    }

    fun setQuiet(value: Int) {
        _quiet.value = value
        triggerFilter()
    }

    fun setTasty(value: Int) {
        _tasty.value = value
        triggerFilter()
    }

    fun setCheap(value: Int) {
        _cheap.value = value
        triggerFilter()
    }

    fun setMusic(value: Int) {
        _music.value = value
        triggerFilter()
    }

    fun clearFilters() {
        _city.value = null
        _wifi.value = 0
        _seat.value = 0
        _quiet.value = 0
        _tasty.value = 0
        _cheap.value = 0
        _music.value = 0
        triggerFilter()
    }

    private fun triggerFilter() {
        viewModelScope.launch {
            val params =
                FilterParams(
                    city = _city.value,
                    wifi = _wifi.value,
                    seat = _seat.value,
                    quiet = _quiet.value,
                    tasty = _tasty.value,
                    cheap = _cheap.value,
                    music = _music.value,
                )

            Log.d("PocketViewModel", "Trigger filter with: $params")

            when (val result = filterCafeFromDBUseCase(FilterCafeFromDBUseCase.Parameters(params))) {
                is Result.Success -> {
                    Log.d("PocketViewModel", "Filter result size: ${result.data.size}")
                    _filteredCafes.value = result.data
                }

                is Result.Error -> {
                    Log.e("PocketViewModel", "Filter error: ${result.exception}")
                    _filteredCafes.value = emptyList()
                }
            }
        }
    }

    fun getAverage() =
        viewModelScope.launch {
            when (val result = getAverageUseCase.invoke()) {
                is Result.Error -> Log.e("PocketViewModel", "Failed to fetch average")
                is Result.Success -> {
                    _bag.value = result.data
                }
            }
        }

    fun getuniCity() =
        viewModelScope.launch {
            when (val result = getUniqueCityFromDBUseCase.invoke()) {
                is Result.Success -> _cityList.value = result.data
                is Result.Error -> Log.e("PocketViewModel", "Failed to fetch city list")
            }
        }

    fun deleteAllCafe() =
        viewModelScope.launch {
            when (val result = deleteAllFromPocketUseCase.invoke()) {
                is Result.Success -> {
                    clearFilters()
                }

                is Result.Error -> Log.e("PocketViewModel", "Failed to delete all cafes")
            }
        }

    fun deleteCafeShop(cafeName: String) =
        viewModelScope.launch {
            deleteCafeShopFromDBUseCase.invoke(DeleteCafeShopFromDBUseCase.Parameters(cafeName))
            triggerFilter() // 重新取得一次過濾結果
        }

    data class FilterParams(
        val city: String?,
        val wifi: Int,
        val seat: Int,
        val quiet: Int,
        val tasty: Int,
        val cheap: Int,
        val music: Int,
    )
}
