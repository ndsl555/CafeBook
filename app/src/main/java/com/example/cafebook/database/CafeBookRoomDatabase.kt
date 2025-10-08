package com.example.cafebook.database

import androidx.room.Database
import androidx.room.RoomDatabase // Make sure RoomDatabase is imported
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.Entity.CafeShopEntity

@Database(
    entities = [ // Corrected from 'entitles'
        BarEntity::class,
        CafeShopEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class CafeBookRoomDatabase : RoomDatabase(), CafeBookDatabase {
    companion object {
        const val DATABASE_NAME = "CafeBook.db"
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // 例如：新增欄位示範
                    // db.execSQL("ALTER TABLE BarTable ADD COLUMN newColumn TEXT")
                }
            }
    }
}
