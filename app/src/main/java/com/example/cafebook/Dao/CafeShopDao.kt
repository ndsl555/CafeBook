package com.example.cafebook.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cafebook.Entity.CafeShopEntity

@Dao
interface CafeShopDao {
    @Query(
        """
        SELECT * FROM cafeshoptable 
        WHERE wifi >= :wifiValue 
          AND seat >= :seatValue 
          AND quiet >= :quietValue 
          AND tasty >= :tastyValue 
          AND cheap >= :cheapValue 
          AND music >= :musicValue 
          AND (:cityFilter IS NULL OR city = :cityFilter)
          AND (:searchQuery IS NULL OR name LIKE '%' || :searchQuery || '%' OR address LIKE '%' || :searchQuery || '%')
    """,
    )
    fun filterCafesWithSearch(
        wifiValue: Int,
        seatValue: Int,
        quietValue: Int,
        tastyValue: Int,
        cheapValue: Int,
        musicValue: Int,
        cityFilter: String?,
        searchQuery: String?,
    ): List<CafeShopEntity>

    @Query(
        """
        SELECT * FROM cafeshoptable 
        WHERE wifi >= :wifiValue 
          AND seat >= :seatValue 
          AND quiet >= :quietValue 
          AND tasty >= :tastyValue 
          AND cheap >= :cheapValue 
          AND music >= :musicValue 
          AND (:cityFilter IS NULL OR city = :cityFilter)
          AND (:searchQuery IS NULL OR name LIKE '%' || :searchQuery || '%')
    """,
    )
    fun filterCafes(
        wifiValue: Int,
        seatValue: Int,
        quietValue: Int,
        tastyValue: Int,
        cheapValue: Int,
        musicValue: Int,
        cityFilter: String?,
        searchQuery: String?,
    ): List<CafeShopEntity>

    @get:Query("SELECT DISTINCT city FROM cafeshoptable")
    val uniqueCities: List<String>

    @get:Query("SELECT * FROM cafeshoptable")
    val allCafes: List<CafeShopEntity>

    @Insert
    fun insert(cafeShop: CafeShopEntity)

    @get:Query("SELECT AVG(wifi) FROM cafeshoptable")
    val averageWifi: Double

    @get:Query("SELECT AVG(seat) FROM cafeshoptable")
    val averageSeat: Double

    @get:Query("SELECT AVG(quiet) FROM cafeshoptable")
    val averageQuiet: Double

    @get:Query("SELECT AVG(tasty) FROM cafeshoptable")
    val averageTasty: Double

    @get:Query("SELECT AVG(cheap) FROM cafeshoptable")
    val averageCheap: Double

    @get:Query("SELECT AVG(music) FROM cafeshoptable")
    val averageMusic: Double

    @Query("DELETE FROM cafeshoptable")
    fun deleteAll()

    @Query("DELETE FROM cafeshoptable WHERE name = :name")
    fun deleteByName(name: String?)

    @Query(
        """
    SELECT 
        AVG(wifi) AS avgWifi,
        AVG(seat) AS avgSeat,
        AVG(quiet) AS avgQuiet,
        AVG(tasty) AS avgTasty,
        AVG(cheap) AS avgCheap,
        AVG(music) AS avgMusic
    FROM cafeshoptable
    """,
    )
    fun getAverages(): CafeAverages
}

data class CafeAverages(
    val avgWifi: Double,
    val avgSeat: Double,
    val avgQuiet: Double,
    val avgTasty: Double,
    val avgCheap: Double,
    val avgMusic: Double,
)
