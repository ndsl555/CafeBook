package com.example.cafebook.NetWork

import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Utils.Result

interface ICafeRemoteDataSource {
    suspend fun fetchCafes(): Result<List<CafeShopEntity>>
}
