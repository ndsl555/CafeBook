package com.example.cafebook.Repository

import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Utils.Result

interface ICafeShopRepository {
    suspend fun getAllCafes(): Result<List<CafeShopEntity>>

    suspend fun getFilteredCafes(
        wifiValue: Int,
        seatValue: Int,
        quietValue: Int,
        tastyValue: Int,
        cheapValue: Int,
        musicValue: Int,
        cityFilter: String?,
        searchQuery: String?,
    ): Result<List<CafeShopEntity>>

    suspend fun insertCafe(cafeShop: CafeShopEntity)

    suspend fun deleteAll()

    suspend fun deleteByName(name: String)

    suspend fun getUniqueCities(): Result<List<String>>

    suspend fun getAverageWifi(): Result<Double>

    suspend fun getAverageSeat(): Result<Double>

    suspend fun getAverageQuiet(): Result<Double>

    suspend fun getAverageTasty(): Result<Double>

    suspend fun getAverageCheap(): Result<Double>

    suspend fun getAverageMusic(): Result<Double>

    suspend fun getAverages(): Result<CafeAverages>
}
