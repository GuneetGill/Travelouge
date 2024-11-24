package com.example.travelogue.table_country

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: Country): Long

    @Delete
    suspend fun deleteCountry(country: Country)

    @Query("SELECT * FROM country_table")
    fun getAllCountries(): Flow<List<Country>>
}