package com.example.travelogue.folder_table

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    @Delete
    suspend fun deleteFolder(folder: Folder)
//
//    @Query("SELECT * FROM folder_table WHERE user_owner_id_col = :userId")
//    suspend fun getFoldersByUser(userId: Long): LiveData<List<Foldert>>

    @Query("SELECT * FROM folder_table")
    fun getAllFolders(): Flow<List<Folder>>
}