package com.example.cafebook.Repository

import com.example.cafebook.Extension.toResult
import com.example.cafebook.NetWork.CafeService
import com.example.cafebook.NetWork.ICafeRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CafeRemoteDataSource(
    private val cafeService: CafeService,
    private val ioDispatcher: CoroutineDispatcher,
) : ICafeRemoteDataSource {
    override suspend fun fetchCafes() =
        withContext(ioDispatcher) {
            val response = cafeService.getAllCafes()
            return@withContext response.toResult()
        }
}
