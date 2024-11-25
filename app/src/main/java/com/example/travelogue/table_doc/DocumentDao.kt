package com.example.travelogue.table_doc


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long

    @Delete
    suspend fun deleteDocument(document: Document)

    @Query("SELECT * FROM document_table")
    fun getAllDocuments(): Flow<List<Document>>
}
