package com.example.cafebook.Repository

import com.example.cafebook.DataSource.IBarDataSource
import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BarRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: IBarDataSource,
) : IBarRepository {
    override suspend fun insertBar(bar: BarEntity) =
        withContext(ioDispatcher) {
            dataSource.insertBar(bar)
        }

    override suspend fun getLatestBar(): Result<BarEntity> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getLatestBar()
        }
}
