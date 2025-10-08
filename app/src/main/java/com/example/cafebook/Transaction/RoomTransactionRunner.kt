package com.example.cafebook.Transaction

import androidx.room.withTransaction
import com.example.cafebook.database.CafeBookRoomDatabase

class RoomTransactionRunner(private val db: CafeBookRoomDatabase) : DatabaseTransactionRunner {
    override suspend fun <T> invoke(block: suspend () -> T): T {
        return db.withTransaction {
            block()
        }
    }
}
