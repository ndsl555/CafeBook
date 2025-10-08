package com.example.cafebook.NetWork
import com.example.cafebook.Entity.CafeShopEntity
import retrofit2.Response
import retrofit2.http.GET

interface CafeService {
    @GET(CafeApiPath.GetAllCafes.PATH)
    suspend fun getAllCafes(): Response<List<CafeShopEntity>>
}
