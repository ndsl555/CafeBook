package com.example.cafebook.DataSource

import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Utils.Result

interface ICafeShopDataSource {
    suspend fun getAllCafeShops(): Result<List<CafeShopEntity>>

    suspend fun getFilteredCafeShops(
        wifiValue: Int,
        seatValue: Int,
        quietValue: Int,
        tastyValue: Int,
        cheapValue: Int,
        musicValue: Int,
        cityFilter: String?,
        searchQuery: String?,
    ): Result<List<CafeShopEntity>>

    suspend fun insertCafeShop(cafeShop: CafeShopEntity)

    suspend fun deleteAllCafeShops()

    suspend fun deleteCafeByName(name: String)

    suspend fun getUniqueCities(): Result<List<String>>

    suspend fun getAverageWifi(): Result<Double>

    suspend fun getAverageSeat(): Result<Double>

    suspend fun getAverageQuiet(): Result<Double>

    suspend fun getAverageTasty(): Result<Double>

    suspend fun getAverageCheap(): Result<Double>

    suspend fun getAverageMusic(): Result<Double>

    suspend fun getAverages(): Result<CafeAverages>
}
