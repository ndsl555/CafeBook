package com.example.cafebook.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "CafeShopTable")
data class CafeShopEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "name")
    var name: String = "",
    @ColumnInfo(name = "city")
    var city: String = "",
    @ColumnInfo(name = "wifi")
    var wifi: Float = 0f,
    @ColumnInfo(name = "seat")
    var seat: Float = 0f,
    @ColumnInfo(name = "quiet")
    var quiet: Float = 0f,
    @ColumnInfo(name = "tasty")
    var tasty: Float = 0f,
    @ColumnInfo(name = "cheap")
    var cheap: Float = 0f,
    @ColumnInfo(name = "music")
    var music: Float = 0f,
    @ColumnInfo(name = "address")
    var address: String = "",
    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,
    @ColumnInfo(name = "url")
    var url: String = "",
    @ColumnInfo(name = "limitedTime")
    var limitedTime: String = "",
    @ColumnInfo(name = "socket")
    var socket: String = "",
    @ColumnInfo(name = "standingDesk")
    var standingDesk: String = "",
    @ColumnInfo(name = "mrt")
    var mrt: String = "",
    @ColumnInfo(name = "openTime")
    var openTime: String = "",
)
