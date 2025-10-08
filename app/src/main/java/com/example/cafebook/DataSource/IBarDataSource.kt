package com.example.cafebook.DataSource

import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Utils.Result

interface IBarDataSource {
    suspend fun insertBar(bar: BarEntity)

    suspend fun getLatestBar(): Result<BarEntity>
}
