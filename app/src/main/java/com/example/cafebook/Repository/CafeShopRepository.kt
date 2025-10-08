package com.example.cafebook.Repository

import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.DataSource.ICafeShopDataSource
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CafeShopRepository(
    private val dataSource: ICafeShopDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ICafeShopRepository {
    override suspend fun getAllCafes(): Result<List<CafeShopEntity>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAllCafeShops()
        }

    override suspend fun getFilteredCafes(
        wifiValue: Int,
        seatValue: Int,
        quietValue: Int,
        tastyValue: Int,
        cheapValue: Int,
        musicValue: Int,
        cityFilter: String?,
        searchQuery: String?,
    ): Result<List<CafeShopEntity>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getFilteredCafeShops(
                wifiValue,
                seatValue,
                quietValue,
                tastyValue,
                cheapValue,
                musicValue,
                cityFilter,
                searchQuery,
            )
        }

    override suspend fun insertCafe(cafeShop: CafeShopEntity) =
        withContext(ioDispatcher) {
            dataSource.insertCafeShop(cafeShop)
        }

    override suspend fun deleteAll() =
        withContext(ioDispatcher) {
            dataSource.deleteAllCafeShops()
        }

    override suspend fun deleteByName(name: String) =
        withContext(ioDispatcher) {
            dataSource.deleteCafeByName(name)
        }

    override suspend fun getUniqueCities(): Result<List<String>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getUniqueCities()
        }

    override suspend fun getAverageWifi(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageWifi()
        }

    override suspend fun getAverageSeat(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageSeat()
        }

    override suspend fun getAverageQuiet(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageQuiet()
        }

    override suspend fun getAverageTasty(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageTasty()
        }

    override suspend fun getAverageCheap(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageCheap()
        }

    override suspend fun getAverageMusic(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverageMusic()
        }

    override suspend fun getAverages(): Result<CafeAverages> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getAverages()
        }
}
