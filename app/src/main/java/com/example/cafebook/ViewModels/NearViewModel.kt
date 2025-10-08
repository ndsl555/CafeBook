package com.example.cafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafebook.UseCase.CafeApiUseCase
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.invoke
import com.example.cafebook.ViewModels.CafeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NearViewModel(
    private val cafeApiUseCase: CafeApiUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CafeUiState())
    val uiState: StateFlow<CafeUiState> = _uiState.asStateFlow()

    init {
        fetchCafeData()
    }

    fun fetchCafeData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
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
                            errorMessage = result.exception.message ?: "未知錯誤",
                        )
                    }
                }
            }
        }
    }
}
