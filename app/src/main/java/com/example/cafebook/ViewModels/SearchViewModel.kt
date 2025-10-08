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

class SearchViewModel(
    private val addCafeToPocketUseCase: AddCafeToPocketUseCase,
    private val cafeApiUseCase: CafeApiUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CafeUiState())
    val uiState: StateFlow<CafeUiState> = _uiState.asStateFlow()

    // 篩選條件
    private val _searchQuery = MutableStateFlow<String>("")
    private val _selectedCity = MutableStateFlow<String?>(null)
    private val _wifiThreshold = MutableStateFlow(0)
    private val _seatThreshold = MutableStateFlow(0)
    private val _quietThreshold = MutableStateFlow(0)
    private val _tastyThreshold = MutableStateFlow(0)
    private val _cheapThreshold = MutableStateFlow(0)
    private val _musicThreshold = MutableStateFlow(0)

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

    // 更新篩選條件的方法
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

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

    // 一個 combine 運算把所有篩選條件與原始的 cafes 結合，產生 UI 要顯示的 filteredList
    val filteredCafes: StateFlow<List<CafeShopEntity>> =
        combine(
            _uiState.map { it.cafes },
            _searchQuery,
            _selectedCity,
            _wifiThreshold,
            _seatThreshold,
            _quietThreshold,
            _tastyThreshold,
            _cheapThreshold,
            _musicThreshold,
        ) { values: Array<Any?> ->

            val cafes = values[0] as List<CafeShopEntity>
            val q = values[1] as String
            val city = values[2] as String?
            val wifi = values[3] as Int
            val seat = values[4] as Int
            val quiet = values[5] as Int
            val tasty = values[6] as Int
            val cheap = values[7] as Int
            val music = values[8] as Int

            cafes.filter { cafe ->
                val matchesSearch = cafe.name.contains(q, ignoreCase = true)
                val matchesCity = (city.isNullOrEmpty() || cafe.city == city)
                val matchesWifi = cafe.wifi >= wifi
                val matchesSeat = cafe.seat >= seat
                val matchesQuiet = cafe.quiet >= quiet
                val matchesTasty = cafe.tasty >= tasty
                val matchesCheap = cafe.cheap >= cheap
                val matchesMusic = cafe.music >= music

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
