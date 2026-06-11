package com.example.cafebook.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.UseCase.AddCafeToPocketUseCase
import com.example.cafebook.UseCase.CafeApiUseCase
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.invoke
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 定義篩選條件的封裝類別
data class FilterCriteria(
    val city: String? = null,
    val wifi: Int = 0,
    val seat: Int = 0,
    val quiet: Int = 0,
    val tasty: Int = 0,
    val cheap: Int = 0,
    val music: Int = 0,
)

class SearchViewModel(
    private val addCafeToPocketUseCase: AddCafeToPocketUseCase,
    private val cafeApiUseCase: CafeApiUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CafeUiState())
    val uiState: StateFlow<CafeUiState> = _uiState.asStateFlow()

    // 篩選條件
    private val _searchQuery = MutableStateFlow<String>("")

    // 暫存的篩選條件 (UI Slider 調整時更新這些變數，但不會立即觸發過濾)
    private val _selectedCity = MutableStateFlow<String?>(null)
    private val _wifiThreshold = MutableStateFlow(0)
    private val _seatThreshold = MutableStateFlow(0)
    private val _quietThreshold = MutableStateFlow(0)
    private val _tastyThreshold = MutableStateFlow(0)
    private val _cheapThreshold = MutableStateFlow(0)
    private val _musicThreshold = MutableStateFlow(0)

    // 3. 真正應用於清單過濾的條件 (按下篩選按鈕後才更新)
    private val _appliedFilters = MutableStateFlow(FilterCriteria())

    init {
        fetchCafeData()
    }

    private fun fetchCafeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = cafeApiUseCase.invoke()) {
                is Result.Success -> {
                    val cafes = result.data
                    val cities = cafes.map { it.city }.toSet().toList().sorted()

                    _uiState.update {
                        it.copy(
                            cafes = cafes,
                            cities = cities,
                            isLoading = false,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Unknown error",
                        )
                    }
                }
            }
        }
    }

    // 更新搜尋文字 (即時反應到 filteredCafes)
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 更新暫存條件的方法 (Slider 調整時呼叫)
    fun setCity(city: String?) {
        _selectedCity.value = city
    }

    fun setWifiThreshold(value: Int) {
        _wifiThreshold.value = value
    }

    fun setSeatThreshold(value: Int) {
        _seatThreshold.value = value
    }

    fun setQuietThreshold(value: Int) {
        _quietThreshold.value = value
    }

    fun setTastyThreshold(value: Int) {
        _tastyThreshold.value = value
    }

    fun setCheapThreshold(value: Int) {
        _cheapThreshold.value = value
    }

    fun setMusicThreshold(value: Int) {
        _musicThreshold.value = value
    }

    // 當按下「篩選」按鈕時呼叫，將目前的暫存值套用到正式篩選器中
    fun applyFilters() {
        _appliedFilters.value =
            FilterCriteria(
                city = _selectedCity.value,
                wifi = _wifiThreshold.value,
                seat = _seatThreshold.value,
                quiet = _quietThreshold.value,
                tasty = _tastyThreshold.value,
                cheap = _cheapThreshold.value,
                music = _musicThreshold.value,
            )
    }

    // 結合「原始資料」、「即時搜尋文字」與「已應用的 Slider 篩選條件」
    val filteredCafes: StateFlow<List<CafeShopEntity>> =
        combine(
            _uiState.map { it.cafes },
            _searchQuery,
            _appliedFilters,
        ) { cafes, query, filters ->
            cafes.filter { cafe ->
                val matchesSearch = cafe.name.contains(query, ignoreCase = true)
                val matchesCity = (filters.city.isNullOrEmpty() || cafe.city == filters.city)
                val matchesWifi = cafe.wifi >= filters.wifi
                val matchesSeat = cafe.seat >= filters.seat
                val matchesQuiet = cafe.quiet >= filters.quiet
                val matchesTasty = cafe.tasty >= filters.tasty
                val matchesCheap = cafe.cheap >= filters.cheap
                val matchesMusic = cafe.music >= filters.music

                matchesSearch && matchesCity && matchesWifi && matchesSeat && matchesQuiet && matchesTasty && matchesCheap && matchesMusic
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList(),
        )

    fun addCafeToPocket(cafe: CafeShopEntity) =
        viewModelScope.launch {
            addCafeToPocketUseCase.invoke(AddCafeToPocketUseCase.Parameters(cafe))
        }
}

data class CafeUiState(
    val cafes: List<CafeShopEntity> = emptyList(),
    val cities: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
