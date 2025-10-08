package com.example.cafebook.DataSource

import com.example.cafebook.Dao.BarCodeDao
import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BarDataSource(
    private val dao: BarCodeDao,
    private val ioDispatcher: CoroutineDispatcher,
) : IBarDataSource {
    override suspend fun insertBar(bar: BarEntity) =
        withContext(ioDispatcher) {
            dao.insertBar(bar)
        }

    override suspend fun getLatestBar(): Result<BarEntity> =
        withContext(ioDispatcher) {
            return@withContext try {
                val bar = dao.getLatestBar()
                Result.Success(bar)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
