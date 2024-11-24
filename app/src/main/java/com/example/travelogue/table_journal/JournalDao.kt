package com.example.travelogue.table_journal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: Journal)

    @Query("SELECT * FROM journal WHERE countryId = :countryId")
    suspend fun getJournalsByCountry(countryId: Long): List<Journal>

    @Delete
    suspend fun deleteJournal(journal: Journal)
}
