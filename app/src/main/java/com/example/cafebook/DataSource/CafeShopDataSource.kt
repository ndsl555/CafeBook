package com.example.cafebook.DataSource

import com.example.cafebook.Dao.CafeAverages
import com.example.cafebook.Dao.CafeShopDao
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.Result.Error
import com.example.cafebook.Utils.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CafeShopDataSource(
    private val dao: CafeShopDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ICafeShopDataSource {
    override suspend fun getAllCafeShops(): Result<List<CafeShopEntity>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val cafes = dao.allCafes
                Success(cafes)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getFilteredCafeShops(
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
            return@withContext try {
                val filtered =
                    dao.filterCafesWithSearch(
                        wifiValue,
                        seatValue,
                        quietValue,
                        tastyValue,
                        cheapValue,
                        musicValue,
                        cityFilter,
                        searchQuery,
                    )
                Success(filtered)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun insertCafeShop(cafeShop: CafeShopEntity) =
        withContext(ioDispatcher) {
            dao.insert(cafeShop)
        }

    override suspend fun deleteAllCafeShops() =
        withContext(ioDispatcher) {
            dao.deleteAll()
        }

    override suspend fun deleteCafeByName(name: String) =
        withContext(ioDispatcher) {
            dao.deleteByName(name)
        }

    override suspend fun getUniqueCities(): Result<List<String>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val cities = dao.uniqueCities
                Success(cities)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageWifi(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageWifi)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageSeat(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageSeat)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageQuiet(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageQuiet)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageTasty(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageTasty)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageCheap(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageCheap)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverageMusic(): Result<Double> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.averageMusic)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getAverages(): Result<CafeAverages> {
        return withContext(ioDispatcher) {
            return@withContext try {
                Success(dao.getAverages())
            } catch (e: Exception) {
                Error(e)
            }
        }
    }
}
