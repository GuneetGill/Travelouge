package com.example.travelogue.table_country

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: Country): Long

    @Delete
    suspend fun deleteCountry(country: Country)

    @Query("SELECT * FROM country_table WHERE user_owner_id_col=:userId")
    fun getAllCountries(userId: Long): List<Country>
}