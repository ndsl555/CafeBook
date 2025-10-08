package com.example.cafebook.NetWork

sealed class CafeApiPath {
    abstract val path: String

    data object GetAllCafes : CafeApiPath() {
        const val PATH = "cafes"
        override val path: String = PATH
    }
}
