package com.example.cafebook.Repository

import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Utils.Result

interface IBarRepository {
    suspend fun insertBar(bar: BarEntity)

    suspend fun getLatestBar(): Result<BarEntity>
}
