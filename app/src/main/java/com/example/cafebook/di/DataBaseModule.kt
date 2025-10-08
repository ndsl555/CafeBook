package com.example.cafebook.di

import androidx.room.Room
import com.example.cafebook.DataSource.BarDataSource
import com.example.cafebook.DataSource.CafeShopDataSource
import com.example.cafebook.DataSource.IBarDataSource
import com.example.cafebook.DataSource.ICafeShopDataSource
import com.example.cafebook.Transaction.DatabaseTransactionRunner
import com.example.cafebook.Transaction.RoomTransactionRunner
import com.example.cafebook.database.CafeBookDatabase
import com.example.cafebook.database.CafeBookRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            val builder =
                Room.databaseBuilder(
                    androidContext(),
                    CafeBookRoomDatabase::class.java,
                    CafeBookRoomDatabase.DATABASE_NAME,
                )
            builder.addMigrations(CafeBookRoomDatabase.MIGRATION_1_2)
                .build()
        }
        single { get<CafeBookRoomDatabase>() as CafeBookDatabase }
        single<DatabaseTransactionRunner> { RoomTransactionRunner(get()) }

        // Dao
        factory { get<CafeBookRoomDatabase>().cafeShopDao() }
        factory { get<CafeBookRoomDatabase>().barCodeDao() }

        // DataSource

        factory<ICafeShopDataSource> { CafeShopDataSource(get(), get(koinIO)) }
        factory<IBarDataSource> { BarDataSource(get(), get(koinIO)) }
    }
