package com.example.cafebook.database

import com.example.cafebook.Dao.BarCodeDao
import com.example.cafebook.Dao.CafeShopDao

interface CafeBookDatabase {
    fun barCodeDao(): BarCodeDao

    fun cafeShopDao(): CafeShopDao
}
