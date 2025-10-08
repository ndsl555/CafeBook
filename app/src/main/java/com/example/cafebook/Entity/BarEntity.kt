package com.example.cafebook.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BarTable")
data class BarEntity(
    @PrimaryKey
    var id: Int = 1,
    @ColumnInfo(name = "barCodeData")
    var barCodeData: String = "",
)
